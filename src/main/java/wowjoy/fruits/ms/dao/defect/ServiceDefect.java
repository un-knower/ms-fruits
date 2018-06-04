package wowjoy.fruits.ms.dao.defect;

import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.comment.DefectComment;
import wowjoy.fruits.ms.module.comment.FruitComment;
import wowjoy.fruits.ms.module.defect.*;
import wowjoy.fruits.ms.module.defect.FruitDefect.ChangeInfo;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.resource.FruitResource;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.DefectDict.Status;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Exception.Check;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;
import wowjoy.fruits.ms.module.versions.FruitVersions;
import wowjoy.fruits.ms.util.ApplicationContextUtils;
import wowjoy.fruits.ms.util.GsonUtils;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.*;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public abstract class ServiceDefect implements InterfaceDao {

    private static final Map<Status, Supplier<String>> includeStatus = Maps.newHashMapWithExpectedSize(4);

    public ServiceDefect() {
        includeStatus.put(Status.SOLVED, () -> StringUtils.join(new String[]{Status.NEW.name(), Status.REOPEN.name(), Status.DELAY.name()}, ","));
        includeStatus.put(Status.DISREGARD, () -> StringUtils.join(new String[]{Status.NEW.name(), Status.REOPEN.name(), Status.DELAY.name()}, ","));
        includeStatus.put(Status.DELAY, () -> StringUtils.join(new String[]{Status.NEW.name(), Status.REOPEN.name()}, ","));
        includeStatus.put(Status.REOPEN, () -> StringUtils.join(new String[]{Status.SOLVED.name(), Status.DISREGARD.name()}, ","));
    }

    /*如果创建人离职，则缺陷状态扭转和编辑权限对所有人开放*/
    /*如果处理人离职，则需要创建人更改处理人*/
    /*新开 ->  已解决(处理人)、不予处理（处理人）、延期处理（处理人）、已关闭（创建人）*/
    /*已解决 ->  已关闭（创建人）、重打开（创建人）*/
    /*已关闭 ->  缺陷结束不允许再打开*/
    /*不予处理 ->  已关闭（创建人）、重打开（创建人）*/
    /*延期处理 ->  已解决（处理人）、不予处理（处理人）、已关闭（创建人）*/
    /*重打开 ->  已解决（处理人）、不予处理（处理人）、延期处理（处理人）、已关闭（创建人）*/
    private BiConsumer<Status, Status> checkStatus = (currentStatus, toStatus) -> Optional.of(includeStatus.get(toStatus))
            .map(Supplier::get)
            .filter(include -> include.contains(currentStatus.name()))
            .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.DEFECT_NO_CHANGE_TO_STATUS.name()));

    /*添加必须包含缺陷添加、资源添加、缺陷 AND 资源关联信息添加，使当前操作在同一个事务中管理，同时回滚提交*/
    protected abstract void insert(FruitDefect.Insert insert);

    /*不包含描述信息*/
    public abstract ArrayList<FruitDefect> findConsumer(Consumer<FruitDefectExample> exampleConsumer);

    protected abstract Optional<FruitDefect> findWithBlobs(String uuid);

    public abstract Page<FruitDefect> findConsumerPage(Consumer<FruitDefect.Search> exampleConsumer);

    /*修改缺陷、删除添加资源*/
    protected abstract void update(Consumer<FruitDefect.Update> updateConsumer, Consumer<FruitDefectExample> exampleConsumer);

    protected abstract ArrayList<FruitDefectResource> findResourceByDefectId(ArrayList<String> defectIds);

    protected abstract List<FruitProject> findProjectByProjectId(ArrayList<String> projectIds);

    protected abstract List<FruitUserDao> findUserByUserIds(ArrayList<String> userIds);

    protected abstract Optional<FruitUserDao> findUserByUserId(Consumer<FruitUserExample> exampleConsumer);

    protected abstract List<FruitVersions> findVersionByVersionId(ArrayList<String> versionIds);

    public abstract void defectCount(Consumer<DefectStatusCount> consumer);

    public abstract Map<String, ArrayList<FruitLogs.Info>> findLogsByDefectId(String defectId);

    public abstract int findStatusRepeatCount(String defectId, Status status);

    public abstract void insertComment(FruitComment.Insert insert, String defectId);

    public abstract ArrayList<DefectComment> findComment(String defectId);

    public abstract DefectDuplicate findDuplicate(String defectId);

    public abstract ArrayList<String> findResourceId(FruitDict.Resource type, String defectId);


    /*--------------------- 全局判断 ----------------------*/
    /*判断用户是否离职，离职返回true*/
    private final Predicate<String> createUserStill = (userId) -> findUserByUserId(fruitUserExample -> fruitUserExample.createCriteria().andUserIdEqualTo(userId).andStatusEqualTo(FruitDict.UserDict.STILL.name()).andIsDeletedEqualTo(Systems.N.name())).isPresent();

    public void beforeInsert(FruitDefect.Insert insert) {
        /*若被当前判断拦截，则说明前端参数未做限制*/
        this.insertCheck(insert);
        /*提取描述中的图片数据*/
        insert.setDescription(FruitResource.Upload.obtainImage(insert.getDescription(), upload -> {
            FruitDefect.Upload defectUpload = new FruitDefect.Upload();
            defectUpload.setUuid(upload.getUuid());
            defectUpload.setSize(upload.getSize());
            defectUpload.setDrType(FruitDict.Resource.DESCRIPTION);
            defectUpload.setType(upload.getType());
            defectUpload.setOutputStream(upload.getOutputStream());
            defectUpload.setNowName(defectUpload.getUuid());
            defectUpload.setOriginName(upload.getOriginName());
            insert.setUpload(defectUpload);
        }));
        insert.setDefectStatus(Status.NEW);    //默认为新建状态
        insert.setAfterVersionId(insert.getBeforeVersionId());    //默认和影响版本一致
        insert.setUserId(ApplicationContextUtils.getCurrentUser().getUserId()); //缺陷创建人
        this.insert(insert);
        /*更新综合查询字段*/
        this.updateDuplicate(insert.getUuid());
        /*清除insert 文件流，否则保存入数据库长度太长，影响日志查询效率*/
        Optional.ofNullable(insert.getUpload())
                .ifPresent(uploads -> uploads.forEach(upload -> {
                    upload.setOutputStream(null);
                    upload.setEncodeData(null);
                }));
    }

    public void beforeUpdate(FruitDefect.Update intoUpdate) {
        this.updateCheck(intoUpdate);
        intoUpdate.setDescription(
                /*获取附件，并将附件存入添加资源列表中*/
                FruitResource.Upload.obtainImage(intoUpdate.getDescription(), upload -> {
                    FruitDefect.Upload defectUpload = new FruitDefect.Upload();
                    defectUpload.setUuid(upload.getUuid());
                    defectUpload.setSize(upload.getSize());
                    defectUpload.setDrType(FruitDict.Resource.DESCRIPTION);
                    defectUpload.setType(upload.getType());
                    defectUpload.setOutputStream(upload.getOutputStream());
                    defectUpload.setNowName(defectUpload.getUuid());
                    defectUpload.setOriginName(upload.getOriginName());
                    Optional.of(intoUpdate)
                            .filter(intoDefect -> intoDefect.getUpload() == null)
                            .ifPresent(intoDefect -> intoDefect.setUpload(Lists.newArrayList()));
                    intoUpdate.getUpload().add(defectUpload);
                }));
        /*更新缺陷*/
        this.update(defect -> {
            defect.setBeforeVersionId(intoUpdate.getBeforeVersionId());
            defect.setDefectName(intoUpdate.getDefectName());
            defect.setHandlerUserId(intoUpdate.getHandlerUserId());
            defect.setDefectType(intoUpdate.getDefectType());
            defect.setDefectLevel(intoUpdate.getDefectLevel());
            defect.setRiskIndex(intoUpdate.getRiskIndex());
            defect.setDescription(intoUpdate.getDescription());
            defect.setUpload(intoUpdate.getUpload());
            defect.setUuid(intoUpdate.getUuid());
            defect.setEndDateTime(intoUpdate.getEndDateTime());

            defect.setRemoveResource(intoUpdate.getRemoveResource());
        }, fruitDefectExample -> fruitDefectExample.createCriteria().andUuidEqualTo(intoUpdate.getUuid()).andIsDeletedEqualTo(Systems.N.name()));
        /*更新综合查询字段*/
        this.updateDuplicate(intoUpdate.getUuid());
        /*清除insert 文件流，否则保存入数据库长度太长，影响日志查询效率*/
        Optional.ofNullable(intoUpdate.getUpload())
                .ifPresent(uploads -> uploads.forEach(upload -> {
                    upload.setOutputStream(null);
                    upload.setEncodeData(null);
                }));
    }

    private String obtainRemoveResourceId(String removeResource, String description, String defectId) {
        Optional.ofNullable(defectId)
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException(Check.SYSTEM_NULL.name()));
        String resourceIdJoin = this.findResourceId(FruitDict.Resource.DESCRIPTION, defectId).stream().filter(id -> StringUtils.isBlank(description) || !description.contains(id))
                .collect(joining(","));
        return Optional.ofNullable(removeResource)
                .filter(StringUtils::isNotBlank)
                .map(str -> resourceIdJoin + "," + str)
                .orElse(resourceIdJoin);
    }

    /*更新综合查询字段*/
    private void updateDuplicate(String defectId) {
        DefectDuplicate duplicate = this.findDuplicate(defectId);
        update(defect -> defect.setDuplicate(MessageFormat.format("{0}{1}{2}{3}",
                Optional.ofNullable(duplicate.getNumber()).orElse(0),
                Optional.ofNullable(duplicate.getCreateUserName()).orElse(""),
                Optional.ofNullable(duplicate.getHandlerUserName()).orElse(""),
                Optional.ofNullable(duplicate.getDefectName()).orElse(""))
        ), fruitDefectExample -> fruitDefectExample.createCriteria().andUuidEqualTo(duplicate.getUuid()));
    }

    private void updateCheck(FruitDefect.Update update) {
        ofNullable(update)
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_UPDATE_NULL.name()));
        FruitDefect fruitDefect = this.existsDefect(update.getUuid());
        Optional.of(update)
                .filter(defect -> defect.getDefectName() == null || defect.getDefectName().length() > 0)
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.DEFECT_NAME_NULL.name()));
        Optional.of(update)
                .filter(defect -> defect.getBeforeVersionId() == null || defect.getBeforeVersionId().length() > 0)
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.DEFECT_BEFORE_NULL.name()));
        Optional.of(update)
                .filter(defect -> defect.getHandlerUserId() == null || defect.getHandlerUserId().length() > 0)
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.DEFECT_HANDLER_USER_NULL.name()));
        ofNullable(update.getHandlerUserId())
                .filter(StringUtils::isNotBlank)
                .map(handlerUserId ->   /*如果改变处理人，则需要验证当前用户是不是缺陷创建人、或则创建人离职，如果都不满足，则属于非法操作，立即拒绝当前操作*/
                        Optional.ofNullable(fruitDefect.getUserId())
                                .filter(createUserId -> createUserId.equals(ApplicationContextUtils.getCurrentUser().getUserId()) || this.createUserStill.test(createUserId))
                                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.DEFECT_CREATE_PERSON.name())));
        Optional.of(update)
                .filter(defect -> defect.getProjectId() == null || defect.getProjectId().length() > 0)
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.DEFECT_PROJECT_NULL.name()));
    }

    /*为了满足当时的业务，后期业务有扩展在修改*/
    private void insertCheck(FruitDefect insert) {
        ofNullable(insert)
                .orElseThrow(() -> new CheckException(Check.SYSTEM_NULL.name()));
        ofNullable(insert.getDefectLevel())
                .orElseThrow(() -> new CheckException(Check.DEFECT_LEVEL_NULL.name()));
        ofNullable(insert.getRiskIndex())
                .orElseThrow(() -> new CheckException(Check.DEFECT_RISK_INDEX_NULL.name()));
        ofNullable(insert.getDefectType())
                .orElseThrow(() -> new CheckException(Check.DEFECT_TYPE_NULL.name()));
        ofNullable(insert.getDefectName())
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException(Check.DEFECT_NAME_NULL.name()));
        ofNullable(insert.getBeforeVersionId())
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException(Check.DEFECT_BEFORE_NULL.name()));
        ofNullable(insert.getHandlerUserId())
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException(Check.DEFECT_HANDLER_USER_NULL.name()));
        ofNullable(insert.getProjectId())
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException(Check.DEFECT_PROJECT_NULL.name()));
    }

    /**
     * 缺陷列表
     * 分页
     *
     * @param search
     * @return
     */
    public Page<FruitDefect> finds(FruitDefect.Search search, UnaryOperator<String> orderBy) {
        Page<FruitDefect> defects = this.findConsumerPage(intoSearch -> {
            intoSearch.setPageNum(search.getPageNum());
            intoSearch.setPageSize(search.getPageSize());
            intoSearch.setIndexIn(Optional
                    .ofNullable(search.getIndex())
                    .filter(StringUtils::isNotBlank)
                    .map(index -> index.split(","))
                    .map(Lists::newArrayList)
                    .orElseGet(() -> Stream.of(FruitDict.DefectDict.Index.values()).map(Enum::name).collect(toCollection(ArrayList::new))));
            intoSearch.setLevelIn(Optional
                    .ofNullable(search.getLevel())
                    .filter(StringUtils::isNotBlank)
                    .map(level -> level.split(","))
                    .map(Lists::newArrayList)
                    .orElseGet(() -> Stream.of(FruitDict.DefectDict.Level.values()).map(Enum::name).collect(toCollection(ArrayList::new))));
            intoSearch.setTypeIn(Optional
                    .ofNullable(search.getType())
                    .filter(StringUtils::isNotBlank)
                    .map(type -> type.split(","))
                    .map(Lists::newArrayList)
                    .orElseGet(() -> Stream.of(FruitDict.DefectDict.Type.values()).map(Enum::name).collect(toCollection(ArrayList::new))));
            intoSearch.setStatusIn(Optional
                    .ofNullable(search.getStatus())
                    .filter(StringUtils::isNotBlank)
                    .map(status -> status.split(","))
                    .map(Lists::newArrayList)
                    .orElseGet(() -> Stream.of(FruitDict.DefectDict.Status.values()).map(Enum::name).collect(toCollection(ArrayList::new))));
            intoSearch.setHandlerUserIdIn(Optional
                    .ofNullable(search.getHandlerUserId())
                    .filter(StringUtils::isNotBlank)
                    .map(handlerUser -> handlerUser.split(","))
                    .map(Lists::newArrayList)
                    .orElse(null));
            intoSearch.setUserIdIn(Optional
                    .ofNullable(search.getUserId())
                    .filter(StringUtils::isNotBlank)
                    .map(userId -> userId.split(","))
                    .map(Lists::newArrayList)
                    .orElse(null));
            intoSearch.setBeforeVersionIdIn(Optional
                    .ofNullable(search.getBeforeVersionId())
                    .filter(StringUtils::isNotBlank)
                    .map(versionId -> versionId.split(","))
                    .map(Lists::newArrayList)
                    .orElse(null));
            intoSearch.setProjectIdIn(Optional
                    .ofNullable(search.getProjectId())
                    .filter(StringUtils::isNotBlank)
                    .map(projectId -> projectId.split(","))
                    .map(Lists::newArrayList)
                    .orElse(null));
            intoSearch.setDefectName(Optional
                    .ofNullable(search.getDefectName())
                    .filter(StringUtils::isNotBlank)
                    .map(name -> name + "%")
                    .orElse(null));
            intoSearch.setDuplicate(Optional
                    .ofNullable(search.getDuplicate())
                    .filter(StringUtils::isNotBlank)
                    .map(duplicate -> "%" + duplicate + "%")
                    .orElse(null));
            Optional.of(search)     /*只包含开始时间时，自动填充结束时间为当前时间*/
                    .filter(defect -> StringUtils.isNotBlank(defect.getStartTime()) && StringUtils.isBlank(defect.getEndTime()))
                    .ifPresent(defect -> {
                        intoSearch.setStartTime(LocalDate.parse(defect.getStartTime()).atTime(0, 0, 0).toString());
                        intoSearch.setEndTime(LocalDateTime.now().toString());
                    });
            Optional.of(search)     /*只包含结束时间时，只查询小于结束时间*/
                    .filter(defect -> StringUtils.isBlank(defect.getStartTime()) && StringUtils.isNotBlank(defect.getEndTime()))
                    .ifPresent(defect -> intoSearch.setEndTime(LocalDate.parse(defect.getEndTime()).atTime(23, 59, 59).toString()));
            Optional.of(search)     /*范围查询，包含开始和结束时间*/
                    .filter(defect -> StringUtils.isNotBlank(defect.getStartTime()) && StringUtils.isNotBlank(defect.getEndTime()))
                    .ifPresent(defect -> {
                        intoSearch.setStartTime(LocalDate.parse(defect.getStartTime()).atTime(0, 0, 0).toString());
                        intoSearch.setEndTime(LocalDate.parse(defect.getEndTime()).atTime(23, 59, 59).toString());
                    });
            intoSearch.setOrderByClause(orderBy.apply(search.sortConstruePro(null, "create_date_time desc")));
        });
        List<FruitDefect.Info> defectInfo = defects.getResult().stream().map(defect -> (FruitDefect.Info) GsonUtils.newGson().fromJson(GsonUtils.newGson().toJsonTree(defect), TypeToken.of(FruitDefect.Info.class).getType())).collect(toList());
        CompletableFuture.allOf(
                CompletableFuture
                        .supplyAsync(this.joinProject(defects.parallelStream().map(FruitDefect::getProjectId).collect(toCollection(ArrayList::new))))
                        .thenAccept(projectMap -> defectInfo.forEach(defect -> ofNullable(projectMap.get(defect.getProjectId())).ifPresent(defect::setProject))),
                CompletableFuture
                        .supplyAsync(this.joinUser(defects))
                        .thenAccept(userMap -> defectInfo.forEach(defect -> {

                            ofNullable(defect.getUserId())
                                    .filter(StringUtils::isNotBlank)
                                    .map(userMap::get)
                                    .ifPresent(defect::setCreateUser);
                            ofNullable(defect.getHandlerUserId())
                                    .filter(StringUtils::isNotBlank)
                                    .map(userMap::get)
                                    .ifPresent(defect::setHandlerUser);
                        })),
                CompletableFuture
                        .supplyAsync(this.joinVersion(defects))
                        .thenAccept(versionMap -> defectInfo.forEach(defect -> {
                            ofNullable(defect.getAfterVersionId())
                                    .filter(StringUtils::isNotBlank)
                                    .map(versionMap::get)
                                    .ifPresent(defect::setAfterVersion);
                            ofNullable(defect.getBeforeVersionId())
                                    .filter(StringUtils::isNotBlank)
                                    .map(versionMap::get)
                                    .ifPresent(defect::setBeforeVersion);
                        }))
        ).join();
        defects.getResult().clear();
        defects.getResult().addAll(defectInfo);
        return defects;
    }

    /**
     * 缺陷详情
     *
     * @param uuid
     * @return
     */
    public FruitDefect.Info find(String uuid) {
        ofNullable(uuid)
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException(Check.SYSTEM_NULL.name()));
        return this.findWithBlobs(uuid)
                .map(defect -> (FruitDefect.Info) GsonUtils.newGson().fromJson(GsonUtils.newGson().toJsonTree(defect), TypeToken.of(FruitDefect.Info.class).getType()))
                .map((FruitDefect.Info info) -> {
                    long startTime = System.currentTimeMillis();
                    CompletableFuture.allOf(
                            CompletableFuture
                                    .supplyAsync(this.joinProject(Lists.newArrayList(info.getProjectId())))
                                    .thenAccept(projectMap -> {
                                        ofNullable(projectMap.get(info.getProjectId())).ifPresent(info::setProject);
                                        logger.info("joinProject：" + (System.currentTimeMillis() - startTime));
                                    }),
                            CompletableFuture.supplyAsync(this.joinUser(Lists.newArrayList(info))).thenAccept(userMap -> {
                                ofNullable(info.getUserId())
                                        .filter(StringUtils::isNotBlank)
                                        .map(userMap::get)
                                        .ifPresent(info::setCreateUser);
                                ofNullable(info.getHandlerUserId())
                                        .filter(StringUtils::isNotBlank)
                                        .map(userMap::get)
                                        .ifPresent(info::setHandlerUser);
                                logger.info("joinUser：" + (System.currentTimeMillis() - startTime));
                            }),
                            CompletableFuture.supplyAsync(this.joinVersion(Lists.newArrayList(info)))
                                    .thenAccept(versionMap -> {
                                        ofNullable(info.getAfterVersionId())
                                                .filter(StringUtils::isNotBlank)
                                                .map(versionMap::get)
                                                .ifPresent(info::setAfterVersion);
                                        ofNullable(info.getBeforeVersionId())
                                                .filter(StringUtils::isNotBlank)
                                                .map(versionMap::get)
                                                .ifPresent(info::setBeforeVersion);
                                        logger.info("joinVersion：" + (System.currentTimeMillis() - startTime));
                                    }),
                            CompletableFuture.supplyAsync(this.joinResource(info.getUuid()))
                                    .thenAccept(resourceMap -> {
                                        ofNullable(resourceMap.get(info.getUuid()))
                                                .ifPresent(info::setFiles);
                                        logger.info("joinResource：" + (System.currentTimeMillis() - startTime));
                                    }),
                            CompletableFuture.supplyAsync(this.joinLogs(info.getUuid())).thenAccept(logsMap -> {
                                ofNullable(logsMap.get(info.getUuid()))
                                        .ifPresent(info::setLogs);
                                logger.info("joinLogs：" + (System.currentTimeMillis() - startTime));
                            }),
                            CompletableFuture.supplyAsync(() -> this.findStatusRepeatCount(info.getUuid(), Status.REOPEN)).thenAccept(count -> {
                                info.setReOpenCount(count);
                                logger.info("findStatusRepeatCount：" + (System.currentTimeMillis() - startTime));
                            }),
                            CompletableFuture.supplyAsync(this.joinComment(info.getUuid())).thenAccept(comments -> {
                                info.setComments(comments);
                                logger.info("joinComment：" + (System.currentTimeMillis() - startTime));
                            })
                    ).join();
                    return info;
                }).orElse(null);
    }

    /**
     * 缺陷转换为已解决
     * 状态限制：新开、重打开、延期处理
     * 用户限制：只有处理人可以切换状态到已解决
     * 2018年05月23日17:10:54：已解决的修复版本迁移至已关闭时选择
     *
     * @param info
     */
    public void toSolved(ChangeInfo info) {
        FruitDefect defect = this.existsDefect(info.getUuid());
        ofNullable(defect.getHandlerUserId())
                .filter(userId -> userId.equals(ApplicationContextUtils.getCurrentUser().getUserId()))
                .orElseThrow(() -> new CheckException(Check.DEFECT_HANDLER_PERSON.name()));
        this.update(update -> update.setDefectStatus(Status.SOLVED), fruitDefectExample -> fruitDefectExample.createCriteria().andUuidEqualTo(info.getUuid()).andIsDeletedEqualTo(Systems.N.name()));
    }

    /**
     * 缺陷转换为已关闭
     * 状态限制：ALL
     * 用户限制：只能由创建人关闭
     *
     * @param info
     */
    public void toClosed(ChangeInfo info) {
        FruitDefect defect = this.existsDefect(info.getUuid());
        String currentUserId = ApplicationContextUtils.getCurrentUser().getUserId();
        ofNullable(defect.getUserId())
                .filter(userId -> userId.equals(currentUserId) || createUserStill.test(userId)) //创建人是当前用户 OR 创建人离职
                .orElseThrow(() -> new CheckException(Check.DEFECT_CREATE_PERSON.name()));
        ofNullable(defect.getAfterVersionId())
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException(Check.DEFECT_AFTER_NULL.name()));
        this.update(update -> {
            update.setDefectStatus(Status.CLOSED);
            update.setAfterVersionId(info.getAfterVersionId());
            /*更新入参，配合操作日志的关闭日期*/
            info.setClosedDateTime(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            update.setClosedDateTime(info.getClosedDateTime());
        }, fruitDefectExample -> fruitDefectExample.createCriteria().andUuidEqualTo(defect.getUuid()).andIsDeletedEqualTo(Systems.N.name()));
    }

    /**
     * 缺陷转换为不予解决
     * 状态限制：新开、重打开、延期处理
     * 用户限制：只能由处理人决定是否不予解决
     *
     * @param info
     */
    public void toDisregard(ChangeInfo info) {
        FruitDefect defect = this.existsDefect(info.getUuid());
        this.checkStatus.accept(defect.getDefectStatus(), Status.DISREGARD);   //检查状态
        ofNullable(defect.getHandlerUserId())
                .filter(userId -> userId.equals(ApplicationContextUtils.getCurrentUser().getUserId()))
                .orElseThrow(() -> new CheckException(Check.DEFECT_HANDLER_PERSON.name()));
        this.update(update -> update.setDefectStatus(Status.DISREGARD), fruitDefectExample -> fruitDefectExample.createCriteria().andUuidEqualTo(defect.getUuid()).andIsDeletedEqualTo(Systems.N.name()));
    }

    /**
     * 缺陷转换为延期处理
     * 状态限制：新开、重打开
     * 用户限制：只能由处理人决定是否延期处理
     *
     * @param info
     */
    public void toDelay(ChangeInfo info) {
        FruitDefect defect = this.existsDefect(info.getUuid());
        this.checkStatus.accept(defect.getDefectStatus(), Status.DELAY);   //检查状态
        ofNullable(defect.getHandlerUserId())
                .filter(userId -> userId.equals(ApplicationContextUtils.getCurrentUser().getUserId()))
                .orElseThrow(() -> new CheckException(Check.DEFECT_HANDLER_PERSON.name()));
        this.update(update -> update.setDefectStatus(Status.DELAY), fruitDefectExample -> fruitDefectExample.createCriteria().andUuidEqualTo(defect.getUuid()).andIsDeletedEqualTo(Systems.N.name()));
    }

    /**
     * 缺陷转换为重新打开
     * 状态限制：已解决、不予解决
     * 用户限制：只能由创建人决定是否重新打开
     *
     * @param info
     */
    public void toReOpen(ChangeInfo info) {
        FruitDefect defect = this.existsDefect(info.getUuid());
        this.checkStatus.accept(defect.getDefectStatus(), Status.REOPEN);    //检查状态
        ofNullable(defect.getUserId())
                .filter(userId -> userId.equals(ApplicationContextUtils.getCurrentUser().getUserId()) || createUserStill.test(userId))
                .orElseThrow(() -> new CheckException(Check.DEFECT_CREATE_PERSON.name()));
        this.update(update -> update.setDefectStatus(Status.REOPEN), fruitDefectExample -> fruitDefectExample.createCriteria().andUuidEqualTo(info.getUuid()));
    }

    /*检查缺陷是否存在*/
    private FruitDefect existsDefect(String defectId) {
        return ofNullable(defectId)
                .filter(StringUtils::isNotBlank)
                .map(id -> this.findConsumer(fruitDefectExample -> fruitDefectExample.createCriteria().andUuidEqualTo(id).andIsDeletedEqualTo(Systems.N.name())))
                .flatMap(defects -> defects.stream().findAny())
                .orElseThrow(() -> new CheckException(Check.SYSTEM_NOT_EXISTS.name()));
    }

    /**
     * @param defectId 缺陷id
     * @return 返回缺陷对应的资源id
     */
    private Supplier<Map<String, ArrayList<FruitDefectResource>>> joinResource(String defectId) {
        return () -> ofNullable(defectId)
                .filter(StringUtils::isNotBlank)
                .map(Lists::newArrayList)
                .map(this::findResourceByDefectId)
                .orElseGet(ArrayList::new)
                .stream().collect(groupingBy(FruitDefectResource::getDefectId, toCollection(ArrayList::new)));
    }

    private Supplier<Map<String, FruitVersions>> joinVersion(ArrayList<FruitDefect> defects) {
        return () -> {
            CopyOnWriteArraySet<String> versionIds = new CopyOnWriteArraySet<>(Sets.newLinkedHashSet());
            defects.parallelStream().forEach(defect -> {
                ofNullable(defect.getAfterVersionId())
                        .filter(StringUtils::isNotBlank)
                        .ifPresent(versionIds::add);
                ofNullable(defect.getBeforeVersionId())
                        .filter(StringUtils::isNotBlank)
                        .ifPresent(versionIds::add);
            });
            return Optional.of(versionIds).filter(ids -> !ids.isEmpty()).map(Lists::newArrayList).map(this::findVersionByVersionId).orElseGet(ArrayList::new).stream().collect(toMap(FruitVersions::getUuid, version -> version));
        };
    }

    private Supplier<Map<String, FruitUser>> joinUser(ArrayList<FruitDefect> defects) {
        return () -> {
            CopyOnWriteArraySet<String> userIds = new CopyOnWriteArraySet<>(Sets.newLinkedHashSet());
            defects.parallelStream().forEach(defect -> {
                ofNullable(defect.getUserId())
                        .filter(StringUtils::isNotBlank)
                        .ifPresent(userIds::add);
                ofNullable(defect.getHandlerUserId())
                        .filter(StringUtils::isNotBlank)
                        .ifPresent(userIds::add);
            });
            return Optional.of(userIds).filter(ids -> !ids.isEmpty()).map(Lists::newArrayList).map(this::findUserByUserIds).orElseGet(ArrayList::new).stream().collect(toMap(FruitUser::getUserId, user -> user));
        };
    }

    private Supplier<Map<String, FruitProject>> joinProject(ArrayList<String> projectIds) {
        return () -> ofNullable(projectIds)
                .filter(ids -> !ids.isEmpty())
                .map(this::findProjectByProjectId).orElseGet(ArrayList::new)
                .stream().collect(toMap(FruitProject::getUuid, project -> project));
    }

    private Supplier<Map<String, ArrayList<FruitLogs.Info>>> joinLogs(String defectId) {
        return () -> ofNullable(defectId)
                .filter(StringUtils::isNotBlank)
                .map(this::findLogsByDefectId)
                .orElseGet(HashMap::new);
    }

    private Supplier<ArrayList<DefectComment>> joinComment(String defectId) {
        return () -> this.findComment(defectId);
    }
}
