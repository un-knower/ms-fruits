package wowjoy.fruits.ms.dao.logs.template;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.defect.ServiceDefect;
import wowjoy.fruits.ms.dao.list.ListDaoImpl;
import wowjoy.fruits.ms.dao.notepad.ServiceNotepad;
import wowjoy.fruits.ms.dao.plan.PlanDaoImpl;
import wowjoy.fruits.ms.dao.project.ProjectDaoImpl;
import wowjoy.fruits.ms.dao.task.TaskDaoImpl;
import wowjoy.fruits.ms.dao.versions.DaoVersions;
import wowjoy.fruits.ms.dao.versions.ServiceVersions;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.DefectDict.Status;
import wowjoy.fruits.ms.module.util.entity.FruitDict.LogsDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Parents;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by wangziwen on 2018/3/8.
 */
public class LogsWriteTemplate {
    private static final Map<Parents, Supplier<? extends LogsHandler>> typeFunction = Maps.newLinkedHashMap();

    static {
        typeFunction.put(Parents.NOTEPAD, NotepadHandler::new);
        typeFunction.put(Parents.PLAN, PlanHandler::new);
        typeFunction.put(Parents.TASK, TaskHandler::new);
        typeFunction.put(Parents.PROJECT, ProjectHandler::new);
        typeFunction.put(Parents.List, ListHandler::new);
        typeFunction.put(Parents.VERSIONS, VersionsHandler::new);
        typeFunction.put(Parents.DEFECT, DefectHandler::new);
    }

    public static Function<String, Optional<? extends AbstractEntity>> getTypeFunction(Parents type, LogsDict operate) {
        return typeFunction.get(type).get().getOperateFunction(operate);
    }

    static abstract class LogsHandler {
        protected final Map<LogsDict, Function<String, Optional<? extends AbstractEntity>>> operateFunction = Maps.newLinkedHashMap();

        public Function<String, Optional<? extends AbstractEntity>> getOperateFunction(LogsDict operate) {
            return operateFunction.get(operate);
        }
    }

    /*日报处理，根据日报操作类型，查询需要记录的日志信息*/
    static class NotepadHandler extends LogsHandler {
        private ServiceNotepad notepadDao = ApplicationContextUtils.getContext().getBean(ServiceNotepad.class);

        public NotepadHandler() {
            operateFunction.put(LogsDict.ADD, findFunction);
            operateFunction.put(LogsDict.UPDATE, findFunction);
            operateFunction.put(LogsDict.DELETE, findFunction);
        }

        Function<String, Optional<? extends AbstractEntity>> findFunction = uuid -> {
            if (StringUtils.isBlank(uuid)) return Optional.empty();
            return notepadDao.finds(example -> example.createCriteria().andUuidEqualTo(uuid)).stream().findAny();
        };
    }

    /*计划处理，根据计划操作类型，查询需要记录的日志信息*/
    static class PlanHandler extends LogsHandler {
        private PlanDaoImpl planDao = ApplicationContextUtils.getContext().getBean(PlanDaoImpl.class);

        public PlanHandler() {
            operateFunction.put(LogsDict.ADD, findFunction);
            operateFunction.put(LogsDict.UPDATE, findFunction);
            operateFunction.put(LogsDict.DELETE, findFunction);
            operateFunction.put(LogsDict.COMPLETE, findFunction);
            operateFunction.put(LogsDict.END, findFunction);
            operateFunction.put(LogsDict.PENDING, findFunction);
        }

        Function<String, Optional<? extends AbstractEntity>> findFunction = uuid -> {
            if (StringUtils.isBlank(uuid)) return Optional.empty();
            return planDao.findByExample(example -> example.createCriteria().andUuidEqualTo(uuid)).stream().findAny();
        };
    }

    /*任务处理，根据任务操作类型，查询需要记录的日志信息*/
    static class TaskHandler extends LogsHandler {
        private TaskDaoImpl taskDao = ApplicationContextUtils.getContext().getBean(TaskDaoImpl.class);

        public TaskHandler() {
            operateFunction.put(LogsDict.ADD, findFunction);
            operateFunction.put(LogsDict.UPDATE, findFunction);
            operateFunction.put(LogsDict.DELETE, findFunction);
            operateFunction.put(LogsDict.COMPLETE, findFunction);
            operateFunction.put(LogsDict.START, findFunction);
            operateFunction.put(LogsDict.END, findFunction);
            operateFunction.put(LogsDict.MOVE_TASK, findFunction);
            operateFunction.put(LogsDict.TRANSFER, findFunction);
        }

        Function<String, Optional<? extends AbstractEntity>> findFunction = uuid -> {
            if (StringUtils.isBlank(uuid)) return Optional.empty();
            return taskDao.findByExample(example -> example.createCriteria().andUuidEqualTo(uuid)).stream().findAny();
        };
    }

    /*项目处理*/
    static class ProjectHandler extends LogsHandler {
        private ProjectDaoImpl projectDao = ApplicationContextUtils.getContext().getBean(ProjectDaoImpl.class);

        public ProjectHandler() {
            operateFunction.put(LogsDict.ADD, findFunction);
            operateFunction.put(LogsDict.UPDATE, findFunction);
            operateFunction.put(LogsDict.COMPLETE, findFunction);
            operateFunction.put(LogsDict.DELETE, findFunction);
            operateFunction.put(LogsDict.DELETE, findFunction);
            operateFunction.put(LogsDict.DELETE, findFunction);
        }

        Function<String, Optional<? extends AbstractEntity>> findFunction = uuid -> {
            if (StringUtils.isBlank(uuid)) return Optional.empty();
            return projectDao.finds(example -> example.createCriteria().andUuidEqualTo(uuid)).stream().findAny();
        };
    }

    static class ListHandler extends LogsHandler {
        private ListDaoImpl listDao = ApplicationContextUtils.getContext().getBean(ListDaoImpl.class);

        public ListHandler() {
            operateFunction.put(LogsDict.ADD, findFunction);
            operateFunction.put(LogsDict.UPDATE, findFunction);
            operateFunction.put(LogsDict.DELETE, findFunction);
        }

        Function<String, Optional<? extends AbstractEntity>> findFunction = uuid -> {
            if (StringUtils.isBlank(uuid)) return Optional.empty();
            return listDao.finds(listExample -> listExample.createCriteria().andUuidEqualTo(uuid)).stream().findAny();
        };
    }

    static class VersionsHandler extends LogsHandler {
        private ServiceVersions serviceVersions = ApplicationContextUtils.getContext().getBean(DaoVersions.class);

        public VersionsHandler() {
            operateFunction.put(LogsDict.ADD, findFunction);
            operateFunction.put(LogsDict.UPDATE, findFunction);
        }

        Function<String, Optional<? extends AbstractEntity>> findFunction = uuid -> Optional.ofNullable(uuid)
                .filter(StringUtils::isNotBlank)
                .map(id -> serviceVersions.findByExample(versionsExample -> versionsExample.createCriteria().andIsDeletedEqualTo(FruitDict.Systems.N.name()).andUuidEqualTo(id)))
                .map(versions -> versions.stream().findAny())
                .orElseGet(Optional::empty);
    }

    static class DefectHandler extends LogsHandler {
        private ServiceDefect serviceDefect = ApplicationContextUtils.getContext().getBean(ServiceDefect.class);

        public DefectHandler() {
            operateFunction.put(LogsDict.ADD, findFunction);
            operateFunction.put(LogsDict.UPDATE, findFunction);
            operateFunction.put(LogsDict.TO_SOLVED, uuid -> findBiFunction.apply(uuid, Status.SOLVED));
            operateFunction.put(LogsDict.TO_SOLVED, uuid -> findBiFunction.apply(uuid, Status.SOLVED));
            operateFunction.put(LogsDict.TO_CLOSED, uuid -> findBiFunction.apply(uuid, Status.CLOSED));
            operateFunction.put(LogsDict.TO_DISREGARD, uuid -> findBiFunction.apply(uuid, Status.DISREGARD));
            operateFunction.put(LogsDict.TO_DELAY, uuid -> findBiFunction.apply(uuid, Status.DELAY));
            operateFunction.put(LogsDict.TO_REOPEN, uuid -> findBiFunction.apply(uuid, Status.REOPEN));
        }

        Function<String, Optional<? extends AbstractEntity>> findFunction = uuid -> Optional.ofNullable(uuid)
                .filter(StringUtils::isNotBlank)
                .map(id -> serviceDefect.findConsumer(fruitDefectExample -> fruitDefectExample.createCriteria().andUuidEqualTo(id)))
                .map(defects -> defects.stream().findAny())
                .orElseGet(Optional::empty);

        BiFunction<String, Status, Optional<? extends AbstractEntity>> findBiFunction = (uuid, status) -> Optional.ofNullable(uuid)
                .filter(StringUtils::isNotBlank)
                .map(id -> {
                    /*插入或更新状态重复次数*/
                    serviceDefect.defectCount(count -> {
                        count.setDefectId(id);
                        count.setDefectStatus(status);
                        count.setDuplicateId(id + "_" + status);
                    });
                    return id;
                })
                .map(id -> serviceDefect.findConsumer(fruitDefectExample -> fruitDefectExample.createCriteria().andUuidEqualTo(id)))
                .map(defects -> defects.stream().findAny())
                .orElseGet(Optional::empty);
    }

}
