package wowjoy.fruits.ms.dao.notepad;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.notepad.FruitNotepad;
import wowjoy.fruits.ms.module.notepad.FruitNotepadDao;
import wowjoy.fruits.ms.module.notepad.FruitNotepadExample;
import wowjoy.fruits.ms.module.notepad.FruitNotepadVo;
import wowjoy.fruits.ms.module.resource.FruitResource;
import wowjoy.fruits.ms.module.team.FruitTeamDao;
import wowjoy.fruits.ms.module.team.FruitTeamUser;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.stream.Collectors.*;

/**
 * Created by wangziwen on 2017/8/25.
 */
public abstract class ServiceNotepad implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/
    public abstract List<FruitNotepadDao> finds(Consumer<FruitNotepadExample> exampleConsumer);

    protected abstract void insert(FruitNotepad.Insert insert);

    protected abstract void update(Consumer<FruitNotepad.Update> updateConsumer, Consumer<FruitNotepadExample> notepadExampleConsumer);

    protected abstract void delete(FruitNotepadDao dao);

    protected abstract List<FruitNotepadDao> findsByExampleAndCustom(Consumer<FruitNotepadExample> exampleConsumer, Consumer<List<String>> customConsumer);

    protected abstract Map<String, ArrayList<FruitLogs.Info>> joinLogs(LinkedList<String> ids);

    protected abstract List<FruitUserDao> joinUser(LinkedList<String> ids);

    public abstract FruitTeamDao findTeamInfo(String teamId, Consumer<FruitUserExample> userExampleConsumer);

    protected abstract ArrayList<String> findResourceId(FruitDict.Resource type, String notepadId);

    public final void beforeInsert(FruitNotepad.Insert insert) {
        if (StringUtils.isBlank(insert.getContent()))
            throw new CheckException(FruitDict.Exception.Check.NOTEPAD_ADD_CONTENT.name());
        if (insert.getEstimatedSubmitDate() == null)
            throw new CheckException(FruitDict.Exception.Check.NOTEPAD_ESTIMATED_SUBMIT_DATE.name());
        Optional.ofNullable(insert.getEstimatedSubmitDate())
                .map(date -> this.finds(example -> example.createCriteria().andEstimatedSubmitDateEqualTo(date).andUserIdEqualTo(ApplicationContextUtils.getCurrentUser().getUserId())))
                .filter(notepads -> !notepads.stream().findAny().isPresent())
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.NOTEPAD_DUPLICATE.name()));
        insert.setContent(FruitResource.Upload.obtainImage(insert.getContent(), upload -> {
            FruitNotepad.Upload defectUpload = new FruitNotepad.Upload();
            defectUpload.setUuid(upload.getUuid());
            defectUpload.setSize(upload.getSize());
            defectUpload.setNrType(FruitDict.Resource.DESCRIPTION);
            defectUpload.setType(upload.getType());
            defectUpload.setOutputStream(upload.getOutputStream());
            defectUpload.setNowName(defectUpload.getUuid());
            defectUpload.setOriginName(upload.getOriginName());
            insert.setUpload(defectUpload);
        }));
        insert.setUserId(ApplicationContextUtils.getCurrentUser().getUserId());
        this.insert(insert);
        /*主动清除文本，防止被日志记录写入数据库*/
        Optional.ofNullable(insert.getUploads())
                .ifPresent(uploads -> uploads.stream().peek(upload -> {
                    upload.setEncodeData(null);
                    upload.setOutputStream(null);
                }));
    }

    public final void beforeUpdate(FruitNotepad.Update intoUpdate) {
        Optional.ofNullable(intoUpdate.getContent())
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.NOTEPAD_ADD_CONTENT.name()));
        Optional.ofNullable(intoUpdate.getUuid())
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name()));
        intoUpdate.setContent(FruitResource.Upload.obtainImage(intoUpdate.getContent(), upload -> {
            FruitNotepad.Upload defectUpload = new FruitNotepad.Upload();
            defectUpload.setUuid(upload.getUuid());
            defectUpload.setSize(upload.getSize());
            defectUpload.setNrType(FruitDict.Resource.DESCRIPTION);
            defectUpload.setType(upload.getType());
            defectUpload.setOutputStream(upload.getOutputStream());
            defectUpload.setNowName(defectUpload.getUuid());
            defectUpload.setOriginName(upload.getOriginName());
            intoUpdate.setUpload(defectUpload);
        }));
        this.update(update -> {
            update.setUuid(intoUpdate.getUuid());
            update.setContent(intoUpdate.getContent());
            update.setUploads(intoUpdate.getUploads());
            /*获取被移除的图片*/
            update.setRemoveResource(this.obtainRemoveResource(intoUpdate.getRemoveResource(), intoUpdate.getContent(), intoUpdate.getUuid()));
        }, example -> example.createCriteria().andUuidEqualTo(intoUpdate.getUuid()));
        /*主动清除文本，防止被日志记录写入数据库*/
        Optional.ofNullable(intoUpdate.getUploads())
                .ifPresent(uploads -> uploads.stream().peek(upload -> {
                    upload.setEncodeData(null);
                    upload.setOutputStream(null);
                }));
    }

    private String obtainRemoveResource(String intoRemoveResource, String content, String notepadId) {
        Optional.ofNullable(notepadId)
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name()));
        ArrayList<String> resourceId = this.findResourceId(FruitDict.Resource.DESCRIPTION, notepadId);
        String resourceIds = Optional.ofNullable(content)
                .map(str -> resourceId.stream().filter(id -> !str.contains(id)).collect(joining(","))).orElseGet(() -> resourceId.stream().collect(joining(",")));
        return Optional.ofNullable(intoRemoveResource)
                .filter(StringUtils::isNotBlank)
                .map(ids -> resourceIds + "," + ids)
                .orElse(resourceIds);
    }

    public final void delete(String uuid) {
        FruitNotepadDao dao = FruitNotepad.getDao();
        dao.setUuid(uuid);
        this.delete(dao);
    }

    public final List<FruitNotepadDao> findNotepadByCurrentUserId(FruitNotepadVo vo) {
        FruitUser currentUser = ApplicationContextUtils.getCurrentUser();
        List<FruitNotepadDao> notepadDaoList = findsByExampleAndCustom(example -> {
            FruitNotepadExample.Criteria criteria = example.createCriteria();
            if (vo.getStartDate() != null && vo.getEndDate() != null) {
                criteria.andEstimatedSubmitDateBetween(
                        ToDate.apply(ToLocalDate.apply(vo.getStartDate()).withHour(0).withMinute(0).withSecond(0)),
                        ToDate.apply(ToLocalDate.apply(vo.getEndDate()).withHour(23).withMinute(59).withSecond(59))
                );
            }
            criteria.andUserIdEqualTo(currentUser.getUserId());
            criteria.andIsDeletedEqualTo(Systems.N.name());
            example.setOrderByClause("estimated_submit_date desc,create_date_time desc");
        }, customs -> {
            if (StringUtils.isNotBlank(vo.getState())) {
                /*创建时间小于第二天12点，正常提交*/
                if (FruitDict.NotepadDict.PUNCTUAL_SUBMIT.name().equals(vo.getState()))
                    customs.add("create_date_time < date_add(date_format(`estimated_submit_date`, '%Y-%m-%d 23:59:59'),interval 12 hour)");
                /*创建时间大于第二天12点，补交*/
                if (FruitDict.NotepadDict.PAY_SUBMIT.name().equals(vo.getState()))
                    customs.add("create_date_time >= date_add(date_format(`estimated_submit_date`, '%Y-%m-%d 23:59:59'),interval 12 hour)");
            }
        });
        if (notepadDaoList.isEmpty())
            return notepadDaoList;
        LinkedList<String> notepadIds = notepadDaoList.stream().map(FruitNotepadDao::getUuid).collect(toCollection(LinkedList::new));
        CompletableFuture.allOf(
                CompletableFuture
                        .supplyAsync(this.plugLogsSupplier(notepadIds))
                        .thenAccept(logsMap -> Optional.ofNullable(logsMap).ifPresent(afterLogsMap -> notepadDaoList.parallelStream().forEach(notepad -> Optional.ofNullable(afterLogsMap.get(notepad.getUuid())).ifPresent(notepad::setLogs)))),
                CompletableFuture
                        .supplyAsync(this.plugUserSupplier(notepadIds))
                        .thenAccept(userMap -> Optional.ofNullable(userMap).ifPresent(afterUserMap -> notepadDaoList.parallelStream().forEach(notepad -> Optional.ofNullable(afterUserMap.get(notepad.getUserId())).ifPresent(notepad::setUser)))),
                CompletableFuture.runAsync(() -> notepadDaoList.parallelStream().forEach(notepad -> notepad.selectState()))
        ).join();
        return notepadDaoList;
    }

    public final FruitTeamDao findNotepadByTeamId(FruitNotepadVo vo, String teamId) {
        if (StringUtils.isBlank(teamId)) throw new CheckException(FruitDict.Exception.Check.NOTEPAD_TEAM_NULL.name());
        FruitTeamDao teamInfo = this.findTeamInfo(teamId, example -> {
            final FruitUserExample.Criteria criteria = example.createCriteria();
            Optional.ofNullable(vo.getUserName()).filter(StringUtils::isNotBlank).ifPresent(userName -> criteria.andUserNameLike(MessageFormat.format("%{0}%", userName)));
            criteria.andIsDeletedEqualTo(Systems.N.name());
        });
        /*未查询到用户信息直接退出函数*/
        if (teamInfo.findUsers().orElseGet(ArrayList::new).isEmpty())
            return teamInfo;
        /*查询指定用户的日报*/
        List<FruitNotepadDao> notepadDaoList = this.findsByExampleAndCustom(example -> {
            /*日报查询条件*/
            FruitNotepadExample.Criteria criteria = example.createCriteria();
            if (vo.getStartDate() != null && vo.getEndDate() != null)
                criteria.andEstimatedSubmitDateBetween(vo.getStartDate(), vo.getEndDate());
            criteria.andUserIdIn(teamInfo.findUsers().orElseGet(LinkedList::new).parallelStream().map(FruitTeamUser::getUserId).collect(toList()));
            criteria.andIsDeletedEqualTo(Systems.N.name());
            example.setOrderByClause("estimated_submit_date desc,create_date_time desc");
        }, customs -> {
            /*自定义日报查询范围*/
            if (StringUtils.isNotBlank(vo.getState())) {
                /*创建时间小于第二天12点，正常提交*/
                if (FruitDict.NotepadDict.PUNCTUAL_SUBMIT.name().equals(vo.getState()))
                    customs.add("create_date_time < date_add(date_format(`estimated_submit_date`, '%Y-%m-%d 23:59:59'),interval 12 hour)");
                /*创建时间大于第二天12点，补交*/
                if (FruitDict.NotepadDict.PAY_SUBMIT.name().equals(vo.getState()))
                    customs.add("create_date_time >= date_add(date_format(`estimated_submit_date`, '%Y-%m-%d 23:59:59'),interval 12 hour)");
            }
        });
        /*融合日报、团队*/
        Map<String, List<FruitNotepadDao>> notepadMap = notepadDaoList.stream().collect(groupingBy(FruitNotepadDao::getUserId));
        if (StringUtils.isNotBlank(vo.getState()))
            teamInfo.setUsers(teamInfo.findUsers().orElseGet(LinkedList::new).parallelStream().filter(user -> {
                if (FruitDict.NotepadDict.NOT_SUBMIT.name().equals(vo.getState()))
                    return !notepadMap.containsKey(user.getUserId());
                else if (FruitDict.NotepadDict.PAY_SUBMIT.name().equals(vo.getState())
                        || FruitDict.NotepadDict.PUNCTUAL_SUBMIT.name().equals(vo.getState()))
                    return notepadMap.containsKey(user.getUserId());
                else
                    return true;
            }).collect(toList()));
        teamInfo.findUsers().orElseGet(LinkedList::new).parallelStream().forEach(user -> user.setMultiNotepad(notepadMap.get(user.getUserId())));
        return teamInfo;
    }

    public final Optional<FruitTeamDao> findNotepadMonthByTeamId(LocalDate date, String teamId, FruitNotepadVo vo) {
        if (date == null) return Optional.empty();
        LocalDate startDate = LocalDate.of(date.getYear(), date.getMonth(), 1);
        LocalDate endDate = LocalDate.of(date.getYear(), date.getMonth(), date.lengthOfMonth());
        vo.setStartDate(ToDate.apply(startDate.atTime(0, 0, 0)));
        vo.setEndDate(ToDate.apply(endDate.atTime(23, 59, 59)));
        return Optional.of(this.findNotepadByTeamId(vo, teamId));
    }

    private Callable plugLogs(List<FruitNotepadDao> notepads) {
        return () -> {
            if (notepads == null || notepads.isEmpty()) return false;
            Map<String, ArrayList<FruitLogs.Info>> logs = this.joinLogs(notepads.stream().map(FruitNotepadDao::getUserId).collect(toCollection(LinkedList::new)));
            notepads.parallelStream().forEach(notepad -> notepad.setLogs(logs.get(notepad.getUuid())));
            return true;
        };
    }

    private Supplier<Map<String, ArrayList<FruitLogs.Info>>> plugLogsSupplier(LinkedList<String> notepads) {
        return () -> Optional.ofNullable(notepads).filter(ids -> !ids.isEmpty()).map(this::joinLogs).orElse(null);
    }

    private Callable plugUser(List<FruitNotepadDao> notepads) {
        return () -> {
            if (notepads == null || notepads.isEmpty()) return false;
            Map<String, List<FruitUserDao>> userMap = this.joinUser(notepads.stream().map(FruitNotepadDao::getUserId).collect(toCollection(LinkedList::new)))
                    .parallelStream().collect(groupingBy(FruitUserDao::getUserId));
            notepads.parallelStream().forEach(notepad -> {
                if (userMap.containsKey(notepad.getUserId()))
                    notepad.setUser(userMap.get(notepad.getUserId()).stream().findAny().get());
            });
            return true;
        };
    }

    private Supplier<Map<String, FruitUserDao>> plugUserSupplier(LinkedList<String> notepads) {
        return () -> Optional.ofNullable(notepads).filter(ids -> !ids.isEmpty())
                .map(this::joinUser)
                .map(users -> users.parallelStream().collect(toMap(FruitUser::getUserId, user -> user))).orElse(null);
    }

    private Callable plugUtils(List<FruitNotepadDao> notepads) {
        return () -> {
            if (notepads == null || notepads.isEmpty()) return false;
            notepads.parallelStream().forEach(notepad -> notepad.selectState());
            return true;
        };
    }
}
