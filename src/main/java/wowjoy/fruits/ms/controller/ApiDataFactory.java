package wowjoy.fruits.ms.controller;

import wowjoy.fruits.ms.dao.plan.AbstractDaoPlan;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.plan.FruitPlan;
import wowjoy.fruits.ms.module.plan.FruitPlanTask;
import wowjoy.fruits.ms.module.plan.FruitPlanUser;
import wowjoy.fruits.ms.module.task.FruitTaskUser;

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
            exportInfo.setLogs(Optional.ofNullable(intoInfo.getLogs()).map(logs -> logs.parallelStream().map(log -> {
                FruitLogs.Info info = FruitLogs.newInfo();
                info.setMsg(log.getMsg());
                info.setCreateDateTime(log.getCreateDateTime());
                return info;
            }).collect(toCollection(LinkedList::new))).orElse(null));
            exportInfo.setTasks(Optional.ofNullable(intoInfo.getTasks()).map(tasks -> tasks.parallelStream().map(task -> {
                FruitPlanTask planTask = new FruitPlanTask();
                planTask.setUuid(task.getUuid());
                planTask.setTitle(task.getTitle());
                planTask.setTaskStatus(task.getTaskStatus());
                planTask.setEstimatedEndDate(task.getEstimatedEndDate());
                planTask.setUsers(task.getUsers().stream().map(user -> {
                    FruitTaskUser planUser = new FruitTaskUser();
                    planUser.setUserName(user.getUserName());
                    planUser.setUserId(user.getUserId());
                    return planUser;
                }).collect(toCollection(LinkedList::new)));
                return planTask;
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
}
