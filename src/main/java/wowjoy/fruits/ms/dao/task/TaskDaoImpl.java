package wowjoy.fruits.ms.dao.task;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.list.ListDaoImpl;
import wowjoy.fruits.ms.dao.logs.service.ServiceLogs;
import wowjoy.fruits.ms.dao.logs.service.ServiceTransferLogs;
import wowjoy.fruits.ms.dao.relation.impl.TaskListDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.TaskPlanDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.TaskProjectDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.TaskUserDaoImpl;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.list.FruitList;
import wowjoy.fruits.ms.module.list.FruitListExample;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.logs.transfer.FruitTransferLogs;
import wowjoy.fruits.ms.module.relation.entity.TaskListRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskPlanRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskProjectRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskUserRelation;
import wowjoy.fruits.ms.module.relation.example.TaskListRelationExample;
import wowjoy.fruits.ms.module.relation.example.TaskPlanRelationExample;
import wowjoy.fruits.ms.module.relation.example.TaskProjectRelationExample;
import wowjoy.fruits.ms.module.relation.example.TaskUserRelationExample;
import wowjoy.fruits.ms.module.task.*;
import wowjoy.fruits.ms.module.task.mapper.FruitTaskMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by wangziwen on 2017/8/31.
 * 任务关联表：
 * 1、关联用户
 * 2、关联计划
 * 3、关联项目
 * 4、关联列表
 */
@Service
@Transactional
public class TaskDaoImpl extends AbstractDaoTask {
    private final FruitTaskMapper taskMapper;
    private final TaskPlanDaoImpl<TaskPlanRelation, TaskPlanRelationExample> taskPlanDao;
    private final TaskProjectDaoImpl<TaskProjectRelation, TaskProjectRelationExample> taskProjectDao;
    private final TaskUserDaoImpl<TaskUserRelation, TaskUserRelationExample> taskUserDao;
    private final TaskListDaoImpl<TaskListRelation, TaskListRelationExample> taskListDao;
    private final ListDaoImpl listDao;
    private final ServiceLogs logsDaoImpl;
    private final ServiceTransferLogs transferLogs;

    @Autowired
    public TaskDaoImpl(FruitTaskMapper taskMapper,
                       @Qualifier("taskPlanDaoImpl") TaskPlanDaoImpl<TaskPlanRelation, TaskPlanRelationExample> taskPlanDao,
                       @Qualifier("taskProjectDaoImpl") TaskProjectDaoImpl<TaskProjectRelation, TaskProjectRelationExample> taskProjectDao,
                       @Qualifier("taskUserDaoImpl") TaskUserDaoImpl<TaskUserRelation, TaskUserRelationExample> taskUserDao,
                       @Qualifier("taskListDaoImpl") TaskListDaoImpl<TaskListRelation, TaskListRelationExample> taskListDao,
                       ListDaoImpl listDao,
                       ServiceLogs logsDaoImpl,
                       ServiceTransferLogs transferLogs) {
        this.taskMapper = taskMapper;
        this.taskPlanDao = taskPlanDao;
        this.taskProjectDao = taskProjectDao;
        this.taskUserDao = taskUserDao;
        this.taskListDao = taskListDao;
        this.listDao = listDao;
        this.logsDaoImpl = logsDaoImpl;
        this.transferLogs = transferLogs;
    }

    @Override
    public void insert(FruitTask.Insert insert) {
        /*插入任务*/
        taskMapper.insertSelective(insert);
        this.ifPresent(insert.getListRelation(), Systems.ADD, list -> taskListDao.insert(listRelation -> {
            listRelation.setTaskId(insert.getUuid());
            listRelation.setListId(list.getListId());
        }));
        this.ifPresent(insert.getPlanRelation(), Systems.ADD, plan -> taskPlanDao.insert(planRelation -> {
            planRelation.setTaskId(insert.getUuid());
            planRelation.setPlanId(plan.getPlanId());
        }));
        this.ifPresent(insert.getProjectRelation(), Systems.ADD, project -> taskProjectDao.insert(projectRelation -> {
            projectRelation.setTaskId(insert.getUuid());
            projectRelation.setProjectId(project.getProjectId());
        }));
        this.ifPresent(insert.getUserRelation(), Systems.ADD, user -> taskUserDao.insert(userRelation -> {
            userRelation.setTaskId(insert.getUuid());
            userRelation.setUserId(user.getUserId());
        }));
    }


    @Override
    public void update(FruitTask.Update update, Consumer<FruitTaskExample> taskExampleConsumer) {
        FruitTaskExample example = new FruitTaskExample();
        taskExampleConsumer.accept(example);
        Optional.of(example).filter(task -> task.getOredCriteria().stream().filter(FruitTaskExample.Criteria::isValid).count() > 0).ifPresent(task -> taskMapper.updateByExampleSelective(update, example));
        this.ifPresent(update.getListRelation(), Systems.DELETE, list -> taskListDao.deleted(taskListRelationExample -> {
            Optional.of(update)
                    .map(FruitTask.Update::getUuid)
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new CheckException("taskId can't null"));
            TaskListRelationExample.Criteria criteria = taskListRelationExample.createCriteria();
            Optional.ofNullable(list.getListId())
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(criteria::andListIdEqualTo);
            criteria.andTaskIdEqualTo(update.getUuid());
        }));
        this.ifPresent(update.getPlanRelation(), Systems.DELETE, plan -> taskPlanDao.deleted(planRelationExample -> {
            Optional.of(update)
                    .map(FruitTask.Update::getUuid)
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new CheckException("taskId can't null"));
            TaskPlanRelationExample.Criteria criteria = planRelationExample.createCriteria();
            Optional.ofNullable(plan.getPlanId())
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(criteria::andPlanIdEqualTo);
            criteria.andTaskIdEqualTo(update.getUuid());
        }));
        this.ifPresent(update.getProjectRelation(), Systems.DELETE, project -> taskProjectDao.deleted(taskProjectRelationExample -> {
            Optional.of(update)
                    .map(FruitTask.Update::getUuid)
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new CheckException("taskId can't null"));
            TaskProjectRelationExample.Criteria criteria = taskProjectRelationExample.createCriteria();
            Optional.ofNullable(project.getProjectId())
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(criteria::andProjectIdEqualTo);
            criteria.andTaskIdEqualTo(update.getUuid());
        }));
        this.ifPresent(update.getUserRelation(), Systems.DELETE, user -> taskUserDao.deleted(taskUserRelationExample -> {
            Optional.of(update)
                    .map(FruitTask.Update::getUuid)
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new CheckException("taskId can't null"));
            TaskUserRelationExample.Criteria criteria = taskUserRelationExample.createCriteria();
            Optional.ofNullable(user.getUserId())
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(criteria::andUserIdEqualTo);
            criteria.andTaskIdEqualTo(update.getUuid());
        }));

        this.ifPresent(update.getListRelation(), Systems.ADD, list -> taskListDao.insert(listRelation -> {
            Optional.of(update)
                    .map(FruitTask.Update::getUuid)
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new CheckException("taskId can't null"));
            listRelation.setTaskId(update.getUuid());
            listRelation.setListId(list.getListId());
        }));
        this.ifPresent(update.getPlanRelation(), Systems.ADD, plan -> taskPlanDao.insert(planRelation -> {
            Optional.of(update)
                    .map(FruitTask.Update::getUuid)
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new CheckException("taskId can't null"));
            planRelation.setTaskId(update.getUuid());
            planRelation.setPlanId(plan.getPlanId());
        }));
        this.ifPresent(update.getProjectRelation(), Systems.ADD, project -> taskProjectDao.insert(projectRelation -> {
            Optional.of(update)
                    .map(FruitTask.Update::getUuid)
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new CheckException("taskId can't null"));
            projectRelation.setTaskId(update.getUuid());
            projectRelation.setProjectId(project.getProjectId());
        }));
        this.ifPresent(update.getUserRelation(), Systems.ADD, user -> taskUserDao.insert(userRelation -> {
            Optional.of(update)
                    .map(FruitTask.Update::getUuid)
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new CheckException("taskId can't null"));
            userRelation.setTaskId(update.getUuid());
            userRelation.setUserId(user.getUserId());
        }));
    }

    @Override
    public void delete(FruitTask.Update update) {
        if (StringUtils.isBlank(update.getUuid()))
            throw new CheckException("缺少任务id");
        FruitTaskExample example = new FruitTaskExample();
        FruitTaskExample.Criteria criteria = example.createCriteria();
        criteria.andUuidEqualTo(update.getUuid());
        FruitTaskDao delete = FruitTask.getDao();
        delete.setIsDeleted(Systems.Y.name());
        /*删除项目、计划、用户关联信息*/
        taskMapper.updateByExampleSelective(delete, example);
        taskListDao.deleted(taskListRelationExample -> taskListRelationExample.createCriteria().andTaskIdEqualTo(update.getUuid()));
        taskPlanDao.deleted(planRelationExample -> planRelationExample.createCriteria().andTaskIdEqualTo(update.getUuid()));
        taskProjectDao.deleted(taskProjectRelationExample -> taskProjectRelationExample.createCriteria().andTaskIdEqualTo(update.getUuid()));
        taskUserDao.deleted(taskUserRelationExample -> taskUserRelationExample.createCriteria().andTaskIdEqualTo(update.getUuid()));
    }

    private <T> void ifPresent(Map<Systems, List<T>> relation, Systems delete, Consumer<T> consumer) {
        Optional.ofNullable(relation)
                .filter(relationMap -> relationMap.containsKey(delete))
                .map(relationMap -> relationMap.get(delete))
                .filter(relations -> !relations.isEmpty())
                .ifPresent(relations -> relations.parallelStream().forEach(consumer));
    }

    @Override
    public List<FruitTask> findByExample(Consumer<FruitTaskExample> consumerExample) {
        FruitTaskExample example = new FruitTaskExample();
        consumerExample.accept(example);
        return taskMapper.selectByExample(example);
    }

    @Override
    public List<FruitTaskInfo> findByListExampleAndProjectId
            (Consumer<FruitTaskExample> exampleUnaryOperator, Consumer<FruitListExample> listExampleConsumer, String
                    projectId, int pageNum, int pageSize) {
        if (projectId == null || projectId.isEmpty()) return Lists.newLinkedList();
        FruitTaskExample example = new FruitTaskExample();
        FruitListExample listExample = new FruitListExample();
        exampleUnaryOperator.accept(example);
        listExampleConsumer.accept(listExample);
        PageHelper.startPage(Optional.of(pageNum).filter(num -> num > 0).orElse(1), Optional.of(pageNum).filter(num -> num > 0).orElse(10));
        return taskMapper.selectByListExampleAndProjectId(example, listExample, projectId);
    }

    @Override
    public List<FruitList> findProjectList(String projectId, Consumer<FruitListExample> listExampleConsumer) {
        return listDao.findByProjectId(projectId, listExampleConsumer::accept);
    }

    @Override
    public List<FruitTaskUser> findJoinUserByTaskIds(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty())
            return Lists.newLinkedList();
        return taskMapper.selectJoinUserByTaskIds(taskIds);
    }

    @Override
    public List<FruitTaskPlan> findPlanByTaskIds(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty())
            return Lists.newLinkedList();
        FruitTaskExample example = new FruitTaskExample();
        example.createCriteria().andUuidIn(taskIds).andIsDeletedEqualTo(Systems.N.name());
        return taskMapper.selectPlanByTask(example);
    }

    @Override
    public List<FruitTaskProject> findProjectByTask(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty())
            return Lists.newLinkedList();
        FruitTaskExample example = new FruitTaskExample();
        return taskMapper.selectProjectByTask(example, taskIds);
    }

    @Override
    public List<FruitTaskList> findListByTask(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty())
            return Lists.newLinkedList();
        FruitTaskExample example = new FruitTaskExample();
        example.createCriteria().andUuidIn(taskIds).andIsDeletedEqualTo(Systems.N.name());
        return taskMapper.selectListByTask(example);
    }

    @Override
    public Map<String, ArrayList<FruitLogs.Info>> findJoinLogsByTask(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) return Maps.newLinkedHashMap();
        return logsDaoImpl.findLogs(example -> {
            example.createCriteria().andFruitUuidIn(taskIds).andFruitTypeEqualTo(FruitDict.Parents.TASK.name());
            example.setOrderByClause("flogs.create_date_time desc");
        }, FruitDict.Parents.TASK);
    }

    @Override
    public List<FruitTaskUser> findUserByTaskExampleAndUserIdOrProjectId
            (Consumer<FruitTaskExample> exampleConsumer, List<String> userIds, String projectId) {
        FruitTaskExample example = new FruitTaskExample();
        exampleConsumer.accept(example);
        return taskMapper.findUserByTaskExampleAndUserIdOrProjectId(example, userIds, projectId);
    }

    @Override
    protected void insertTransfer(Consumer<FruitTransferLogs.Insert> insertConsumer) {
        transferLogs.insert(insertConsumer);
    }

    /************************************************************************************************
     *                                       个人中心专供                                            *
     ************************************************************************************************/

    @Override
    protected List<FruitTaskInfo> myTask(Consumer<FruitTaskExample> taskExampleConsumer, String projectId) {
        FruitTaskExample example = new FruitTaskExample();
        taskExampleConsumer.accept(example);
        return taskMapper.myTaskByExample(example, Lists.newArrayList(ApplicationContextUtils.getCurrentUser().getUserId()), projectId);
    }

    @Override
    protected List<FruitTaskInfo> myCreateTask(Consumer<FruitTaskExample> taskExampleConsumer, String projectId) {
        FruitTaskExample example = new FruitTaskExample();
        taskExampleConsumer.accept(example);
        return taskMapper.myCreateTask(example, ApplicationContextUtils.getCurrentUser().getUserId(), projectId);
    }

    /*我创建的项目来自哪些项目，返回项目列表*/
    public List<FruitTaskProject> myCreateTaskFromProject() {
        return taskMapper.myCreateTaskFromProjects(ApplicationContextUtils.getCurrentUser().getUserId());
    }
}