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
import wowjoy.fruits.ms.module.defect.DefectStatusCount;
import wowjoy.fruits.ms.module.defect.FruitDefect;
import wowjoy.fruits.ms.module.defect.FruitDefect.ChangeInfo;
import wowjoy.fruits.ms.module.defect.FruitDefectExample;
import wowjoy.fruits.ms.module.defect.FruitDefectResource;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.resource.FruitResource;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict.DefectDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.DefectDict.Status;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;
import wowjoy.fruits.ms.module.versions.FruitVersions;
import wowjoy.fruits.ms.util.ApplicationContextUtils;
import wowjoy.fruits.ms.util.GsonUtils;

import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
    BiConsumer<Status, Status> checkStatus = (currentStatus, toStatus) -> Optional.of(includeStatus.get(toStatus))
            .map(Supplier::get)
            .filter(include -> include.contains(currentStatus.name()))
            .orElseThrow(() -> new CheckException("current status can't to " + toStatus.name()));

    /*添加必须包含缺陷添加、资源添加、缺陷 AND 资源关联信息添加，使当前操作在同一个事务中管理，同时回滚提交*/
    protected abstract void insert(FruitDefect.Insert insert);

    /*不包含描述信息*/
    public abstract ArrayList<FruitDefect> findConsumer(Consumer<FruitDefectExample> exampleConsumer);

    protected abstract Optional<FruitDefect> findWithBlobs(String uuid);

    protected abstract Page<FruitDefect> findConsumerPage(Consumer<FruitDefectExample> exampleConsumer, int pageNum, int pageSize);

    /*修改缺陷、删除添加资源*/
    protected abstract void update(Consumer<FruitDefect.Update> updateConsumer, Consumer<FruitDefectExample> exampleConsumer);

    protected abstract ArrayList<FruitDefectResource> findResourceByDefectId(ArrayList<String> defectIds);

    protected abstract List<FruitProject> findProjectByProjectId(ArrayList<String> projectIds);

    protected abstract List<FruitUserDao> findUserByUserIds(ArrayList<String> userIds);

    protected abstract List<FruitVersions> findVersionByVersionId(ArrayList<String> versionIds);

    public abstract void defectCount(Consumer<DefectStatusCount> consumer);

    public abstract Map<String, ArrayList<FruitLogs.Info>> findLogsByDefectId(String defectId);

    public abstract int findStatusRepeatCount(String defectId, Status status);

    public abstract void insertComment(FruitComment.Insert insert, String defectId);

    public abstract ArrayList<DefectComment> findComment(String defectId);

    public void beforeInsert(FruitDefect.Insert insert) {
        /*若被当前判断拦截，则说明前端参数未做限制*/
        this.insertCheck(insert);
        /*提取描述中的图片数据*/
        insert.setDescription(FruitResource.Upload.obtainImage(insert.getDescription(), upload -> {
            FruitDefect.Upload defectUpload = new FruitDefect.Upload();
            defectUpload.setSize(upload.getSize());
            defectUpload.setDrType(DefectDict.Resource.DESCRIPTION);
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
        /*清除insert 文件流，否则保存入数据库长度太长，影响日志查询效率*/
        insert.getUpload().forEach(upload -> {
            upload.setOutputStream(null);
            upload.setEncodeData(null);
        });
    }

    public void beforeUpdate(FruitDefect.Update update) {
        this.updateCheck(update);
        /*获取附件，并将附件存入添加资源列表中*/
        FruitResource.Upload.obtainImage(update.getDescription(), upload -> {
            FruitDefect.Upload defectUpload = new FruitDefect.Upload();
            defectUpload.setSize(upload.getSize());
            defectUpload.setDrType(DefectDict.Resource.DESCRIPTION);
            defectUpload.setType(upload.getType());
            defectUpload.setOutputStream(upload.getOutputStream());
            defectUpload.setNowName(defectUpload.getUuid());
            defectUpload.setOriginName(upload.getOriginName());
            Optional.of(update)
                    .filter(defect -> defect.getUpload() == null)
                    .ifPresent(defect -> defect.setUpload(Lists.newArrayList()));
            update.getUpload().add(defectUpload);
        });
        this.update(defect -> {
            defect.setBeforeVersionId(update.getBeforeVersionId());
            defect.setDefectName(update.getDefectName());
            defect.setHandlerUserId(update.getHandlerUserId());
            defect.setDefectType(update.getDefectType());
            defect.setDefectLevel(update.getDefectLevel());
            defect.setRiskIndex(update.getRiskIndex());
            defect.setDescription(update.getDescription());
            defect.setUpload(update.getUpload());
            defect.setRemoveResource(update.getRemoveResource());
            defect.setUuid(update.getUuid());
        }, fruitDefectExample -> fruitDefectExample.createCriteria().andUuidEqualTo(update.getUuid()).andIsDeletedEqualTo(Systems.N.name()));
        /*清除insert 文件流，否则保存入数据库长度太长，影响日志查询效率*/
        update.getUpload().forEach(upload -> {
            upload.setOutputStream(null);
            upload.setEncodeData(null);
        });
    }

    private void updateCheck(FruitDefect.Update update) {
        ofNullable(update)
                .orElseThrow(() -> new CheckException("info can't null"));
        this.existsDefect(update.getUuid());
        Optional.of(update)
                .filter(defect -> defect.getDefectName() == null || defect.getDefectName().length() > 0)
                .orElseThrow(() -> new CheckException("name length can't less then 0"));
        Optional.of(update)
                .filter(defect -> defect.getBeforeVersionId() == null || defect.getBeforeVersionId().length() > 0)
                .orElseThrow(() -> new CheckException("before version can't null"));
        Optional.of(update)
                .filter(defect -> defect.getHandlerUserId() == null || defect.getHandlerUserId().length() > 0)
                .orElseThrow(() -> new CheckException("handler user can't null"));
        Optional.of(update)
                .filter(defect -> defect.getProjectId() == null || defect.getProjectId().length() > 0)
                .orElseThrow(() -> new CheckException("project can't null"));
    }

    /*为了满足当时的业务，后期业务有扩展在修改*/
    private void insertCheck(FruitDefect insert) {
        ofNullable(insert)
                .orElseThrow(() -> new CheckException("info can't null"));
        ofNullable(insert.getDefectLevel())
                .orElseThrow(() -> new CheckException("level can't null"));
        ofNullable(insert.getRiskIndex())
                .orElseThrow(() -> new CheckException("risk index can't null"));
        ofNullable(insert.getDefectType())
                .orElseThrow(() -> new CheckException("type can't null"));
        ofNullable(insert.getDefectName())
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException("name can't null"));
        ofNullable(insert.getBeforeVersionId())
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException("before version can't null"));
        ofNullable(insert.getHandlerUserId())
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException("handler user can't null"));
        ofNullable(insert.getProjectId())
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException("project can't null"));
    }

    /**
     * 缺陷列表
     * 分页
     *
     * @param search
     * @return
     */
    public Page<FruitDefect> finds(FruitDefect.Search search) {
        Page<FruitDefect> defects = this.findConsumerPage(fruitDefectExample -> {
            FruitDefectExample.Criteria criteria = fruitDefectExample.createCriteria();
            ofNullable(search.getProjectId())       //先过滤项目，可以过滤掉大部分数据
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(criteria::andProjectIdEqualTo);
            ofNullable(search.getBeforeVersionId())
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(criteria::andBeforeVersionIdEqualTo);
            Optional.ofNullable(search.getStatus())
                    .filter(StringUtils::isNotBlank)
                    .map(status -> status.split(","))
                    .map(Lists::newArrayList)
                    .ifPresent(criteria::andDefectStatusIn);
            Optional.ofNullable(search.getIndex())
                    .filter(StringUtils::isNotBlank)
                    .map(index -> index.split(","))
                    .map(Lists::newArrayList)
                    .ifPresent(criteria::andRiskIndexIn);
            Optional.ofNullable(search.getType())
                    .filter(StringUtils::isNotBlank)
                    .map(type -> type.split(","))
                    .map(Lists::newArrayList)
                    .ifPresent(criteria::andDefectTypeIn);
            Optional.ofNullable(search.getLevel())
                    .filter(StringUtils::isNotBlank)
                    .map(type -> type.split(","))
                    .map(Lists::newArrayList)
                    .ifPresent(criteria::andDefectLevelIn);
            ofNullable(search.getUserId())  //由于用户可能会出现跨项目，跨版本的缺陷，所以不应该把用户id放在靠前匹配列
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(criteria::andUserIdEqualTo);
            ofNullable(search.getHandlerUserId())
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(criteria::andHandlerUserIdEqualTo);
            Optional.ofNullable(search.getDuplicate())
                    .filter(StringUtils::isNotBlank)
                    .map(duplicate -> "%" + duplicate + "%")
                    .ifPresent(criteria::andDuplicateLike);
            Optional.of(search)
                    .filter(defect -> defect.getStartTime() != null)
                    .filter(defect -> defect.getEndTime() != null)
                    .ifPresent(defect -> criteria.andCreateDateTimeBetween(Date.from(defect.getStartTime().atZone(ZoneId.systemDefault()).toInstant()), Date.from(defect.getEndTime().atZone(ZoneId.systemDefault()).toInstant())));
            fruitDefectExample.setOrderByClause("create_date_time desc");
            if (StringUtils.isNotBlank(search.sortConstrue()))
                fruitDefectExample.setOrderByClause(search.sortConstrue());
        }, search.getPageNum(), search.getPageSize());
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
                .orElseThrow(() -> new CheckException("defect not exists"));
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
     *
     * @param info
     */
    public void toSolved(ChangeInfo info) {
        FruitDefect defect = this.existsDefect(info.getUuid());
        this.checkStatus.accept(defect.getDefectStatus(), Status.SOLVED);   //检查状态
        ofNullable(defect.getHandlerUserId())
                .filter(userId -> userId.equals(ApplicationContextUtils.getCurrentUser().getUserId()))
                .orElseThrow(() -> new CheckException("current user weren't defect handler person"));
        this.update(update -> {
            update.setDefectStatus(Status.SOLVED);
            update.setAfterVersionId(info.getAfterVersionId());
        }, fruitDefectExample -> fruitDefectExample.createCriteria().andUuidEqualTo(info.getUuid()).andIsDeletedEqualTo(Systems.N.name()));
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
        ofNullable(defect.getUserId())
                .filter(userId -> userId.equals(ApplicationContextUtils.getCurrentUser().getUserId()))
                .orElseThrow(() -> new CheckException("current user weren't defect create person"));
        this.update(update -> update.setDefectStatus(Status.CLOSED), fruitDefectExample -> fruitDefectExample.createCriteria().andUuidEqualTo(defect.getUuid()).andIsDeletedEqualTo(Systems.N.name()));
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
                .orElseThrow(() -> new CheckException("current user weren't defect handler person"));
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
                .orElseThrow(() -> new CheckException("current user weren't defect handler person"));
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
                .filter(userId -> userId.equals(ApplicationContextUtils.getCurrentUser().getUserId()))
                .orElseThrow(() -> new CheckException("current user weren't defect create person"));
        this.update(update -> update.setDefectStatus(Status.REOPEN), fruitDefectExample -> fruitDefectExample.createCriteria().andUuidEqualTo(info.getUuid()));
    }

    /*检查缺陷是否存在*/
    private FruitDefect existsDefect(String defectId) {
        return ofNullable(defectId)
                .filter(StringUtils::isNotBlank)
                .map(id -> this.findConsumer(fruitDefectExample -> fruitDefectExample.createCriteria().andUuidEqualTo(id).andIsDeletedEqualTo(Systems.N.name())))
                .flatMap(defects -> defects.stream().findAny())
                .orElseThrow(() -> new CheckException("defect not exists!"));
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
            LinkedHashSet<String> versionIds = Sets.newLinkedHashSet();
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
            LinkedHashSet<String> userIds = Sets.newLinkedHashSet();
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
