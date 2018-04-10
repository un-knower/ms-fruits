package wowjoy.fruits.ms.controller;

import org.assertj.core.util.Lists;
import wowjoy.fruits.ms.dao.plan.AbstractDaoPlan;
import wowjoy.fruits.ms.module.list.FruitListDao;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.plan.FruitPlan;
import wowjoy.fruits.ms.module.plan.FruitPlanTask;
import wowjoy.fruits.ms.module.plan.FruitPlanUser;
import wowjoy.fruits.ms.module.task.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static java.util.stream.Collectors.toCollection;

/**
 * Created by wangziwen on 2018/3/20.
 * 接口出参转换工厂
 */
class ApiDataFactory {
    static class PlanController {
        static final UnaryOperator<FruitPlan.Info> findInfo = intoInfo -> {
            final FruitPlan.Info exportInfo = intoInfo.deepCopy();
            exportInfo.setUuid(intoInfo.getUuid());
            exportInfo.setTitle(intoInfo.getTitle());
            exportInfo.setDescription(intoInfo.getDescription());
            exportInfo.setPlanStatus(exportInfo.getPlanStatus());
            exportInfo.setEndDate(intoInfo.getEndDate());
            exportInfo.setStartDate(intoInfo.getStartDate());
            exportInfo.setEstimatedEndDate(intoInfo.getEstimatedEndDate());
            exportInfo.setEstimatedStartDate(intoInfo.getEstimatedStartDate());
            exportInfo.setParentId(intoInfo.getParentId());
            exportInfo.setLogs(Optional.ofNullable(intoInfo.getLogs()).map(logs -> logs.parallelStream().map(log -> {
                FruitLogs.Info info = FruitLogs.newInfo();
                info.setMsg(log.getMsg());
                info.setUuid(log.getUuid());
                info.setCreateDateTime(log.getCreateDateTime());
                return info;
            }).collect(toCollection(ArrayList::new))).orElse(null));
            exportInfo.setTasks(Optional.ofNullable(intoInfo.getTasks()).map(tasks -> tasks.parallelStream().map(task -> {
                FruitTask.Info export = new FruitTask.Info();
                export.setUuid(task.getUuid());
                export.setTitle(task.getTitle());
                export.setTaskStatus(task.getTaskStatus());
                export.setEstimatedEndDate(task.getEstimatedEndDate());
                export.setUsers(task.getUsers().stream().map(user -> {
                    FruitTaskUser planUser = new FruitTaskUser();
                    planUser.setUserName(user.getUserName());
                    planUser.setUserId(user.getUserId());
                    return planUser;
                }).collect(toCollection(LinkedList::new)));
                return export;
            }).collect(toCollection(ArrayList::new))).orElse(null));
            exportInfo.setUsers(Optional.ofNullable(intoInfo.getUsers()).map(users -> users.parallelStream().map(user -> {
                FruitPlanUser planUser = new FruitPlanUser();
                planUser.setPlanRole(user.getPlanRole());
                planUser.setUserName(user.getUserName());
                planUser.setUserId(user.getUserId());
                planUser.setStatus(user.getStatus());
                return planUser;
            }).collect(toCollection(LinkedList::new))).orElse(null));
            return exportInfo;
        };

        static final UnaryOperator<AbstractDaoPlan.Result> findComposite = planResult -> {
            AbstractDaoPlan.Result export = AbstractDaoPlan.Result.getInstance();
            export.setDataCount(planResult.getDataCount());
            export.setPlans(planResult.getPlans().parallelStream().map(plan -> {
                UnaryOperator<FruitPlanUser> userTemplate = user -> {
                    FruitPlanUser planUser = new FruitPlanUser();
                    planUser.setUserName(user.getUserName());
                    planUser.setUserId(user.getUserId());
                    planUser.setStatus(user.getStatus());
                    return planUser;
                };
                UnaryOperator<FruitPlan.Info> planTemplate = info -> {
                    FruitPlan.Info template = FruitPlan.newInfo();
                    template.setUuid(info.getUuid());
                    template.setParentId(info.getParentId());
                    template.setDays(info.getDays());
                    template.setUsers(Optional.ofNullable(info.getUsers()).map(users -> users.parallelStream().map(userTemplate).collect(toCollection(LinkedList::new))).orElse(null));
                    template.setPlanStatus(info.getPlanStatus());
                    template.setEstimatedStartDate(info.getEstimatedStartDate());
                    template.setEstimatedEndDate(info.getEstimatedEndDate());
                    template.setTitle(info.getTitle());
                    return template;
                };

                FruitPlan.Info info = planTemplate.apply(plan);
                info.setWeeks(Optional.ofNullable(plan.getWeeks()).map(weekPlan -> weekPlan.parallelStream().map(planTemplate).collect(toCollection(ArrayList::new))).orElse(null));
                return info;
            }).collect(toCollection(ArrayList::new)));
            return export;
        };

        static final UnaryOperator<ArrayList<FruitPlan>> find = plans -> plans.parallelStream().map(plan -> {
            FruitPlan.Info info = FruitPlan.newInfo();
            info.setUuid(plan.getUuid());
            info.setPlanStatus(plan.getPlanStatus());
            info.setEstimatedStartDate(plan.getEstimatedStartDate());
            info.setEstimatedEndDate(plan.getEstimatedEndDate());
            info.setTitle(plan.getTitle());
            return info;
        }).collect(toCollection(ArrayList::new));
    }

    static class TaskController {
        static UnaryOperator<FruitTask.Info> findTaskInfo = task -> {
            FruitTask.Info exportInfo = new FruitTask.Info();
            exportInfo.setLogs(Optional.ofNullable(task.getLogs()).map(logs -> logs.parallelStream().map(log -> {
                FruitLogs.Info info = FruitLogs.newInfo();
                info.setMsg(log.getMsg());
                info.setUuid(log.getUuid());
                info.setCreateDateTime(log.getCreateDateTime());
                return info;
            }).collect(toCollection(ArrayList::new))).orElse(null));
            exportInfo.setProject(Optional.ofNullable(task.getProject()).map(project -> {
                FruitTaskProject exportProject = new FruitTaskProject();
                exportProject.setUuid(project.getUuid());
                exportProject.setTitle(project.getTitle());
                return exportProject;
            }).orElse(null));
            exportInfo.setList(Optional.ofNullable(task.getList()).map(list -> {
                FruitTaskList exportList = new FruitTaskList();
                exportList.setTitle(list.getTitle());
                exportList.setUuid(list.getUuid());
                return exportList;
            }).orElse(null));
            exportInfo.setPlan(Optional.ofNullable(task.getPlan()).map(plan -> {
                FruitTaskPlan exportPlan = new FruitTaskPlan();
                exportPlan.setTitle(plan.getTitle());
                exportPlan.setUuid(plan.getUuid());
                return exportPlan;
            }).orElse(null));
            exportInfo.setUsers(Optional.ofNullable(task.getUsers()).map(users -> users.parallelStream().map(user -> {
                FruitTaskUser planUser = new FruitTaskUser();
                planUser.setUserRole(user.getUserRole());
                planUser.setUserName(user.getUserName());
                planUser.setUserId(user.getUserId());
                planUser.setStatus(user.getStatus());
                return planUser;
            }).collect(toCollection(LinkedList::new))).orElse(null));
            exportInfo.setUuid(task.getUuid());
            exportInfo.setTitle(task.getTitle());
            exportInfo.setDescription(task.getDescription());
            exportInfo.setTaskStatus(task.getTaskStatus());
            exportInfo.setEstimatedEndDate(task.getEstimatedEndDate());
            exportInfo.setEndDate(task.getEndDate());
            exportInfo.setDays(task.getDays());
            return exportInfo;
        };

        static UnaryOperator<ArrayList<FruitTaskList>> findJoinProject = lists -> lists.parallelStream().map(list -> {
            FruitTaskList exportList = new FruitTaskList();
            exportList.setTitle(list.getTitle());
            exportList.setUuid(list.getUuid());
            exportList.setTasks(Optional.ofNullable(list.getTasks()).map(tasks -> tasks.parallelStream().map(findTaskInfo).collect(toCollection(ArrayList::new))).orElse(null));
            return exportList;
        }).collect(toCollection(ArrayList::new));
    }
}
