package wowjoy.fruits.ms.dao.logs.template;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.list.ListDaoImpl;
import wowjoy.fruits.ms.dao.logs.dao.DaoTransferLogs;
import wowjoy.fruits.ms.dao.logs.service.ServiceTransferLogs;
import wowjoy.fruits.ms.dao.user.UserDaoImpl;
import wowjoy.fruits.ms.dao.versions.DaoVersions;
import wowjoy.fruits.ms.dao.versions.ServiceVersions;
import wowjoy.fruits.ms.module.defect.FruitDefect;
import wowjoy.fruits.ms.module.list.FruitList;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.logs.FruitLogs.Info;
import wowjoy.fruits.ms.module.logs.FruitLogsDao;
import wowjoy.fruits.ms.module.logs.transfer.FruitTransferLogs;
import wowjoy.fruits.ms.module.logs.transfer.FruitTransferUser;
import wowjoy.fruits.ms.module.plan.FruitPlan;
import wowjoy.fruits.ms.module.plan.FruitPlanDao;
import wowjoy.fruits.ms.module.relation.entity.TaskListRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskUserRelation;
import wowjoy.fruits.ms.module.task.FruitTask;
import wowjoy.fruits.ms.module.task.FruitTaskDao;
import wowjoy.fruits.ms.module.task.FruitTaskVo.TaskTransferVo;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.user.mapper.FruitUserMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.LogsDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;
import wowjoy.fruits.ms.module.versions.FruitVersions;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.stream.Collectors.*;

/**
 * Created by wangziwen on 2017/12/22.
 */
public abstract class LogsReadTemplate {
    private final Map<LogsDict, BiFunction<FruitLogsDao, FruitLogsDao, ArrayList>> templates = Maps.newLinkedHashMap();
    private final static LinkedHashMap<FruitDict.Parents, Supplier<LogsReadTemplate>> instanceFactory = Maps.newLinkedHashMap();

    static {
        instanceFactory.put(FruitDict.Parents.NOTEPAD, NotepadReadTemplate::new);
        instanceFactory.put(FruitDict.Parents.PLAN, PlanReadTemplate::new);
        instanceFactory.put(FruitDict.Parents.TASK, TaskReadTemplate::new);
        instanceFactory.put(FruitDict.Parents.DEFECT, DefectReadTemplate::new);
    }

    public Info msg(FruitLogsDao beforeLogs, FruitLogsDao afterLogs) {
        /*替换个性化参数*/
        return Optional.ofNullable(afterLogs)
                .map(FruitLogsDao::toInfo)
                .map(info -> {
                    info.setMsg(getTemplates(afterLogs.getOperateType()).apply(beforeLogs, afterLogs));
                    return info;
                }).orElseGet(FruitLogs.Info::new);
    }

    public static LogsReadTemplate newInstance(FruitDict.Parents parents) {
        return instanceFactory.get(parents).get();
    }

    private BiFunction<FruitLogsDao, FruitLogsDao, ArrayList> getTemplates(LogsDict logsDict) {
        return templates.get(logsDict);
    }

    <T> T fromJson(String jsonObject, Class<T> tClass) {
        return Optional.ofNullable(jsonObject)
                .filter(StringUtils::isNotBlank)
                .map(object -> (T) new Gson().fromJson(new JsonParser().parse(object), TypeToken.of(tClass).getType()))
                .orElse(null);
    }

    protected static class DefectReadTemplate extends LogsReadTemplate {
        private final static ServiceVersions versions = ApplicationContextUtils.getContext().getBean(DaoVersions.class);
        private final static FruitUserMapper userMapper = ApplicationContextUtils.getContext().getBean(FruitUserMapper.class);

        DefectReadTemplate() {
            super.templates.put(LogsDict.ADD, (beforeLog, afterLog) -> Lists.newArrayList(DefectLog.newInstance(MessageFormat.format("{0} 创建了缺陷，等待解决", afterLog.getUser().getUserName()))));
            super.templates.put(LogsDict.UPDATE, (beforeLog, afterLog) -> {
                LinkedList<DefectLog> msgLists = Lists.newLinkedList();
                FruitDefect.Update afterDefect = this.fromJson(afterLog.getVoObject(), FruitDefect.Update.class);
                Optional.ofNullable(afterDefect)
                        .ifPresent(defect -> {
                            FruitDefect beforeDefect = Optional.ofNullable(beforeLog).map(FruitLogs::getJsonObject).filter(StringUtils::isNotBlank).map(log -> this.fromJson(log, FruitDefect.class)).orElseGet(FruitDefect::new);
                            Optional.ofNullable(defect.getDefectName()).ifPresent(name -> msgLists.add(DefectLog.newInstance(Optional.ofNullable(defectName.apply(afterLog.getUser().getUserName(), beforeDefect.getDefectName())).orElse(""))));
                            Optional.ofNullable(defect.getDefectType()).ifPresent(name -> msgLists.add(DefectLog.newInstance(Optional.ofNullable(defectType.apply(afterLog.getUser().getUserName(), beforeDefect.getDefectType().getValue())).orElse(""))));
                            Optional.ofNullable(defect.getDefectLevel()).ifPresent(name -> msgLists.add(DefectLog.newInstance(Optional.ofNullable(defectLevel.apply(afterLog.getUser().getUserName(), beforeDefect.getDefectLevel().getValue())).orElse(""))));
                            Optional.ofNullable(defect.getRiskIndex()).ifPresent(name -> msgLists.add(DefectLog.newInstance(Optional.ofNullable(defectIndex.apply(afterLog.getUser().getUserName(), beforeDefect.getRiskIndex().getValue())).orElse(""))));
                            Optional.ofNullable(defect.getDescription()).ifPresent(name -> msgLists.add(DefectLog.newInstance(Optional.ofNullable(defectDescription.apply(afterLog.getUser().getUserName())).orElse(""))));
                            Optional.ofNullable(defect.getUpload()).map(uploads -> uploads.stream().filter(upload -> upload.getDrType().equals(FruitDict.Resource.FORM)).collect(toCollection(ArrayList::new))).filter(uploads -> !uploads.isEmpty()).ifPresent(uploads -> msgLists.add(DefectLog.newInstance(Optional.ofNullable(upload.apply(afterLog.getUser().getUserName(), uploads.size())).orElse(""))));
                            Optional.ofNullable(defect.getRemoveResources()).filter(removeResource -> !removeResource.isEmpty()).ifPresent(name -> msgLists.add(DefectLog.newInstance(Optional.ofNullable(removeResource.apply(afterLog.getUser().getUserName(), defect.getRemoveResources().size())).orElse(""))));
                            Optional.ofNullable(defect.getBeforeVersionId()).ifPresent(name -> msgLists.add(DefectLog.newInstance(Optional.ofNullable(beforeVersionId.apply(afterLog.getUser().getUserName(), beforeDefect.getBeforeVersionId())).orElse(""))));
                            Optional.ofNullable(defect.getHandlerUserId()).ifPresent(name -> msgLists.add(DefectLog.newInstance(Optional.ofNullable(handlerUserId.apply(afterLog.getUser().getUserName(), beforeDefect.getHandlerUserId())).orElse(""))));
                            Optional.ofNullable(defect.getEndDateTime()).ifPresent(endDateTime -> msgLists.add(DefectLog.newInstance(Optional.ofNullable(defectEndDateTime.apply(afterLog.getUser().getUserName(), Optional.ofNullable(beforeDefect).map(FruitDefect::getEndDateTime).map(defectEndDateTime -> LocalDateTime.ofInstant(defectEndDateTime.toInstant(), ZoneId.systemDefault()).toLocalDate().toString()).orElse(""))).orElse(""))));
                        });
                return Lists.newArrayList(msgLists);
            });
            super.templates.put(LogsDict.TO_SOLVED, (beforeLog, afterLog) -> Lists.newArrayList(DefectLog.newInstance(MessageFormat.format("{0} 解决了缺陷，等待验证", afterLog.getUser().getUserName()), this.fromJson(afterLog.getVoObject(), FruitDefect.ChangeInfo.class).getComment())));
            super.templates.put(LogsDict.TO_CLOSED, (beforeLog, afterLog) -> {
                FruitDefect.ChangeInfo afterVO = this.fromJson(afterLog.getVoObject(), FruitDefect.ChangeInfo.class);
                DefectLog defectLog = new DefectLog();
                defectLog.setMsg(MessageFormat.format("{0} 关闭了缺陷，修复版本是：{1}，关闭时间：{2}", afterLog.getUser().getUserName(),
                        Optional.ofNullable(afterVO)
                                .map(FruitDefect.ChangeInfo::getAfterVersionId)
                                .map(versionId -> versions.findByExample(versionsExample -> versionsExample.createCriteria().andUuidEqualTo(versionId)))
                                .flatMap(versions -> versions.stream().findAny())
                                .map(FruitVersions::getVersions).orElse("未找到修复版本"), Optional.ofNullable(afterVO.getClosedDateTime()).map(closedDate -> LocalDateTime.ofInstant(closedDate.toInstant(), ZoneId.systemDefault()).toLocalDate().toString()).orElse("历史遗留数据不存在关闭时间")));
                defectLog.setComment(afterVO.getComment());
                return Lists.newArrayList(defectLog);
            });
            super.templates.put(LogsDict.TO_DISREGARD, (beforeLog, afterLog) -> Lists.newArrayList(DefectLog.newInstance(MessageFormat.format("{0} 不予解决", afterLog.getUser().getUserName()), Optional.ofNullable(this.fromJson(afterLog.getVoObject(), FruitDefect.ChangeInfo.class)).map(FruitDefect.ChangeInfo::getComment).orElse("未填写理由"))));
            super.templates.put(LogsDict.TO_DELAY, (beforeLog, afterLog) -> Lists.newArrayList(DefectLog.newInstance(MessageFormat.format("{0} 延期处理", afterLog.getUser().getUserName()), Optional.ofNullable(this.fromJson(afterLog.getVoObject(), FruitDefect.ChangeInfo.class)).map(FruitDefect.ChangeInfo::getComment).orElse("未填写理由"))));
            super.templates.put(LogsDict.TO_REOPEN, (beforeLog, afterLog) -> Lists.newArrayList(DefectLog.newInstance(MessageFormat.format("{0} 重新打开缺陷，等待解决", afterLog.getUser().getUserName()), Optional.ofNullable(this.fromJson(afterLog.getVoObject(), FruitDefect.ChangeInfo.class)).map(FruitDefect.ChangeInfo::getComment).orElse("未填写理由"))));

        }

        BiFunction<String, String, String> defectName = (userName, beforeName) -> MessageFormat.format("{0} 修改了缺陷名称，变更前为：{1}", userName, beforeName);

        BiFunction<String, String, String> defectType = (userName, beforeType) -> MessageFormat.format("{0} 修改了缺陷类型，变更前为：{1}", userName, beforeType);
        BiFunction<String, String, String> defectLevel = (userName, beforeLevel) -> MessageFormat.format("{0} 修改了优先级，变更前为：{1}", userName, beforeLevel);
        BiFunction<String, String, String> defectIndex = (userName, beforeIndex) -> MessageFormat.format("{0} 修改了严重程度，变更前为：{1}", userName, beforeIndex);
        BiFunction<String, String, String> defectEndDateTime = (userName, endDateTime) -> MessageFormat.format("{0} 修改截止时间，变更前为：{1}", userName, endDateTime);
        Function<String, String> defectDescription = (userName) -> MessageFormat.format("{0} 修改了描述", userName);
        BiFunction<String, String, String> beforeVersionId = (userName, beforeVersionId) -> MessageFormat.format("{0} 修改了影响版本，变更前为：{1}", userName, Optional.ofNullable(versions.findByExample(versionsExample -> versionsExample.createCriteria().andUuidEqualTo(beforeVersionId))).flatMap(versions -> versions.stream().findAny()).map(FruitVersions::getVersions).orElse(""));
        BiFunction<String, String, String> handlerUserId = (String userName, String handlerUserId) -> Optional.ofNullable(handlerUserId)
                .filter(StringUtils::isNotBlank)
                .map(userId -> {
                    FruitUserExample example = new FruitUserExample();
                    example.createCriteria().andUserIdEqualTo(userId);
                    return MessageFormat.format("{0} 改变了缺陷处理人，变更前为：{1}", userName,
                            Optional.ofNullable(userMapper.selectByExample(example))
                                    .flatMap(users -> users.stream().findAny())
                                    .map(FruitUser::getUserName)
                                    .orElse(""));
                }).orElse("");
        BiFunction<String, Integer, String> upload = (userName, count) -> MessageFormat.format("{0} 上传了 {1} 个附件", userName, Objects.toString(count));
        BiFunction<String, Integer, String> removeResource = (userName, count) -> MessageFormat.format("{0} 删除了 {1} 个附件", userName, Objects.toString(count));

        static class DefectLog extends FruitDefect.ChangeInfo {
            private String msg;

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }

            public static DefectLog newInstance(String msg) {
                DefectLog defectLog = new DefectLog();
                defectLog.setMsg(msg);
                return defectLog;
            }

            public static DefectLog newInstance(String msg, String comment) {
                DefectLog defectLog = new DefectLog();
                defectLog.setMsg(msg);
                defectLog.setComment(comment);
                return defectLog;
            }
        }
    }

    protected static class NotepadReadTemplate extends LogsReadTemplate {
        NotepadReadTemplate() {
            super.templates.put(LogsDict.ADD, (beforeLog, afterLog) -> Lists.newArrayList(MessageFormat.format("{0} 添加了日报", afterLog.getUser().getUserName())));
            super.templates.put(LogsDict.UPDATE, (beforeLog, afterLog) -> Lists.newArrayList(MessageFormat.format("{0} 修改了日报", afterLog.getUser().getUserName())));
            super.templates.put(LogsDict.DELETE, (beforeLog, afterLog) -> Lists.newArrayList(MessageFormat.format("{0} 删除了日报", afterLog.getUser().getUserName())));
        }

    }

    protected static class PlanReadTemplate extends LogsReadTemplate {
        PlanReadTemplate() {
            super.templates.put(LogsDict.ADD, (beforeLog, afterLog) -> Lists.newArrayList(MessageFormat.format("{0} 添加了计划", afterLog.getUser().getUserName())));
            super.templates.put(LogsDict.UPDATE, (beforeLog, afterLog) -> {
                ArrayList<String> msgList = Lists.newArrayList();
                Optional<FruitPlan.Update> afterUpdate = Optional.ofNullable(afterLog).map(FruitLogs::getVoObject).map(vo -> super.fromJson(vo, FruitPlan.Update.class));
                Optional<FruitPlan.Update> beforeUpdate = Optional.ofNullable(beforeLog).map(FruitLogs::getJsonObject).map(beforeObj -> super.fromJson(beforeObj, FruitPlan.Update.class));
                afterUpdate.map(FruitPlan.Update::getTitle)
                        .filter(Objects::nonNull)
                        .ifPresent(title -> msgList.add(this.title.apply(afterLog, beforeUpdate.map(FruitPlan.Update::getTitle).orElse(""))));
                afterUpdate.map(FruitPlan.Update::getDescription)
                        .filter(Objects::nonNull)
                        .ifPresent(description -> msgList.add(this.description.apply(afterLog, beforeUpdate.map(FruitPlan.Update::getDescription).orElse(""))));
                afterUpdate.map(FruitPlan.Update::getUserRelation)
                        .ifPresent(userRelation -> Optional.ofNullable(this.user.apply(afterLog, userRelation)).ifPresent(msgList::add));
                return msgList;
            });
            super.templates.put(LogsDict.DELETE, (beforeLog, afterLog) -> Lists.newArrayList(MessageFormat.format("{0} 删除了计划", afterLog.getUser().getUserName())));
            super.templates.put(LogsDict.COMPLETE, (beforeLog, afterLog) -> {
                Optional<FruitPlan.Info> info = Optional.ofNullable(afterLog).map(FruitLogs::getJsonObject).map(vo -> super.fromJson(vo, FruitPlan.Info.class));
                return info.map(plan -> {
                    plan.computeDays().obtainPlanStatus();
                    return plan;
                }).map(FruitPlan.Info::getPlanStatus).filter(StringUtils::isNotBlank)
                        .filter(status -> !LogsDict.valueOf(status).equals(LogsDict.COMPLETE))
                        .map(LogsDict::valueOf)
                        .map(status -> super.templates.get(status).apply(beforeLog, afterLog)).orElse(Lists.newArrayList(MessageFormat.format("{0} 按时完成了目标", beforeLog.getUser().getUserName())));
            });
            super.templates.put(LogsDict.DELAY_COMPLETE, (beforeLog, afterLog) -> Optional.ofNullable(afterLog).map(FruitLogs::getJsonObject).map(voObject -> super.fromJson(voObject, FruitPlanDao.class)).map(plan -> {
                plan.computeDays().obtainPlanStatus();
                return plan;
            }).map(plan -> Lists.newArrayList(MessageFormat.format("{0} 延期 {1} 天完成了目标，延期原因：{2}", afterLog.getUser().getUserName(), Math.abs(plan.getDays()), plan.getStatusDescription()))).orElse(Lists.newArrayList()));
            super.templates.put(LogsDict.END, (beforeLog, afterLog) -> Lists.newArrayList(MessageFormat.format("{0} 标记目标为已终止，终止理由：{1}", afterLog.getUser().getUserName(), this.fromJson(afterLog.getJsonObject(), FruitPlanDao.class).getStatusDescription())));
            super.templates.put(LogsDict.PENDING, (beforeLog, afterLog) -> Lists.newArrayList(MessageFormat.format("{0} 标记目标为进行中", afterLog.getUser().getUserName())));
        }

        private BiFunction<FruitLogsDao, String, String> title = (afterLog, title) -> MessageFormat.format("{0} 修改了目标名称，变更前为：{1}", afterLog.getUser().getUserName(), title);
        private BiFunction<FruitLogsDao, String, String> description = (afterLog, description) -> MessageFormat.format("{0} 修改了目标描述，变更前为：{1}", afterLog.getUser().getUserName(), description);
        private BiFunction<FruitLogsDao, Map<Systems, List<String>>, String> user = (afterLog, planUserRelations) -> {
            Predicate<Systems> userPredicate = key -> planUserRelations.containsKey(key) && !planUserRelations.get(key).isEmpty();
            Function<List<String>, String> userFunction = userRelations -> ApplicationContextUtils.getContext().getBean(UserDaoImpl.class).findExample(example -> example.createCriteria().andUserIdIn(
                    new ArrayList<String>(userRelations)
            )).stream().map(FruitUserDao::getUserName).reduce((l, r) -> l + "、" + r).orElse("未查询到用户信息");
            LinkedList<String> appends = Lists.newLinkedList();
            if (userPredicate.test(Systems.ADD))
                appends.add(userFunction.andThen(text -> "添加成员：" + text).apply(planUserRelations.get(Systems.ADD)));
            if (userPredicate.test(Systems.DELETE))
                appends.add(userFunction.andThen(text -> "移除成员：" + text).apply(planUserRelations.get(Systems.DELETE)));
            return Optional.of(appends).filter(msgs -> !msgs.isEmpty()).map(msgs -> MessageFormat.format("{0} 对目标成员进行了变动：{1}", afterLog.getUser().getUserName(), msgs.stream().collect(joining(",")))).orElse(null);
        };

    }

    protected static class TaskReadTemplate extends LogsReadTemplate {
        private static final ServiceTransferLogs serviceTransferLogs = ApplicationContextUtils.getContext().getBean(DaoTransferLogs.class);
        private static final ListDaoImpl listDaoImpl = ApplicationContextUtils.getContext().getBean(ListDaoImpl.class);

        TaskReadTemplate() {
            super.templates.put(LogsDict.ADD, (beforeLog, afterLog) -> Lists.newArrayList(MessageFormat.format("{0} 添加了任务", afterLog.getUser().getUserName())));
            super.templates.put(LogsDict.UPDATE, (beforeLog, afterLog) -> {
                LinkedList<String> msgList = Lists.newLinkedList();
                Optional<FruitTask.Update> afterUpdate = Optional.ofNullable(afterLog.getVoObject()).map(afterTask -> super.fromJson(afterTask, FruitTask.Update.class));
                Optional<FruitTask.Update> beforeUpdate = Optional.ofNullable(beforeLog).map(FruitLogs::getJsonObject).map(beforeTask -> super.fromJson(beforeTask, FruitTask.Update.class));
                afterUpdate.map(FruitTask.Update::getTitle)
                        .ifPresent(title -> msgList.add(this.title.apply(afterLog, beforeUpdate.map(FruitTask.Update::getTitle).orElse(""))));
                afterUpdate.map(FruitTask.Update::getDescription)
                        .ifPresent(description -> msgList.add(this.description.apply(afterLog, beforeUpdate.map(FruitTask.Update::getDescription).orElse(""))));
                afterUpdate.map(FruitTask.Update::getListRelation)
                        .filter(listMap -> listMap.containsKey(Systems.ADD))
                        .filter(listMap -> !listMap.get(Systems.ADD).isEmpty())
                        .ifPresent(listMap -> Optional.ofNullable(list.apply(afterLog, listMap)).ifPresent(msgList::add));
                afterUpdate.map(FruitTask.Update::getUserRelation)
                        .ifPresent(userRelation -> Optional.ofNullable(user.apply(afterLog, userRelation)).ifPresent(msgList::add));
                return msgList.parallelStream().collect(toCollection(ArrayList::new));
            });
            super.templates.put(LogsDict.DELETE, (beforeLog, afterLog) -> Lists.newArrayList(MessageFormat.format("{0} 删除了任务", afterLog.getUser().getUserName())));
            super.templates.put(LogsDict.COMPLETE, (beforeLog, afterLog) -> Lists.newArrayList(MessageFormat.format("{0} 完成了任务", afterLog.getUser().getUserName())));
            super.templates.put(LogsDict.START, (beforeLog, afterLog) -> Lists.newArrayList(MessageFormat.format("{0} 启动了任务", afterLog.getUser().getUserName())));
            super.templates.put(LogsDict.MOVE_TASK, (beforeLog, afterLog) -> Lists.newArrayList(MessageFormat.format("{0} 改变了任务所在列表", afterLog.getUser().getUserName())));
            super.templates.put(LogsDict.END, (beforeLog, afterLog) -> Lists.newArrayList(MessageFormat.format("{0} 终止了任务，终止理由：{1}", afterLog.getUser().getUserName(), this.fromJson(afterLog.getJsonObject(), FruitTaskDao.class).getStatusDescription())));
            super.templates.put(LogsDict.TRANSFER, (beforeLog, afterLog) -> {
                TaskTransferVo taskTransferVo = super.fromJson(afterLog.getVoObject(), TaskTransferVo.class);
                FruitTransferLogs transferLogs = serviceTransferLogs.findInfo(taskTransferVo.getTransferId());
                return Lists.newArrayList(MessageFormat.format(
                        "{0} 将任务转交给：{1}，转交前成员为：{2}，转交理由：{3}。",
                        afterLog.getUser().getUserName(),
                        transferLogs.getTransferUser()
                                .filter(transferUser -> transferUser.containsKey(FruitDict.TransferDict.NEW))
                                .map(transferUser -> transferUser.get(FruitDict.TransferDict.NEW).stream().map(FruitTransferUser::getUserName).collect(joining("、")))
                                .orElse("未找到转交成员列表"),
                        transferLogs.getTransferUser()
                                .filter(transferUser -> transferUser.containsKey(FruitDict.TransferDict.OLD))
                                .map(transferUser -> transferUser.get(FruitDict.TransferDict.OLD).stream().map(FruitTransferUser::getUserName).collect(joining("、")))
                                .orElse("未找到转交前成员列表"),
                        transferLogs.getReason()));
            });
        }

        private BiFunction<FruitLogsDao, String, String> title = (afterLog, title) -> MessageFormat.format("{0} 修改了任务名称，变更前为：{1}", afterLog.getUser().getUserName(), title);
        private BiFunction<FruitLogsDao, String, String> description = (afterLog, description) -> MessageFormat.format("{0} 修改了任务描述，变更前为：{1}", afterLog.getUser().getUserName(), description);
        private BiFunction<FruitLogsDao, Map<Systems, List<TaskUserRelation>>, String> user = (afterLog, taskuserRelationMap) -> {
            Predicate<Systems> userPredicate = key -> taskuserRelationMap.containsKey(key) && !taskuserRelationMap.get(key).isEmpty();
            Function<List<TaskUserRelation>, String> userFunction = userRelations -> ApplicationContextUtils.getContext().getBean(UserDaoImpl.class).findExample(example -> example.createCriteria().andUserIdIn(
                    userRelations
                            .stream()
                            .map(TaskUserRelation::getUserId)
                            .collect(toList())
            )).stream().map(FruitUserDao::getUserName).reduce((l, r) -> l + "、" + r).orElse("未查询到用户信息");
            LinkedList<String> appends = Lists.newLinkedList();
            if (userPredicate.test(Systems.ADD))
                appends.add(userFunction.andThen(text -> "添加成员：" + text).apply(taskuserRelationMap.get(Systems.ADD)));
            if (userPredicate.test(Systems.DELETE))
                appends.add(userFunction.andThen(text -> "移除成员：" + text).apply(taskuserRelationMap.get(Systems.DELETE)));
            return Optional.of(appends).filter(msgs -> !msgs.isEmpty()).map(msgs -> MessageFormat.format("{0} 对任务成员进行了变动，{1}", afterLog.getUser().getUserName(), msgs.stream().collect(joining(",")))).orElse(null);
        };
        private BiFunction<FruitLogsDao, Map<Systems, List<TaskListRelation>>, String> list = (afterLog, update) ->
                Optional.ofNullable(update.get(Systems.BEFORE))
                        .map(beforeList -> listDaoImpl.finds(listExample -> listExample.createCriteria().andUuidIn(beforeList.parallelStream().map(TaskListRelation::getListId).collect(toList()))))
                        .filter(lists -> !lists.isEmpty())
                        .map(lists -> lists.parallelStream().map(FruitList::getTitle).collect(joining(",")))
                        .map(string -> MessageFormat.format("{0} 修改了所属分类，变更之前：{1}", afterLog.getUser().getUserName(), string))
                        .orElse(null);
    }
}
