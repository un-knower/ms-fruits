package wowjoy.fruits.ms.dao.task;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.list.ListDaoImpl;
import wowjoy.fruits.ms.dao.logs.AbstractDaoLogs;
import wowjoy.fruits.ms.dao.relation.impl.TaskListDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.TaskPlanDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.TaskProjectDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.TaskUserDaoImpl;
import wowjoy.fruits.ms.dao.user.UserDaoImpl;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.list.FruitListDao;
import wowjoy.fruits.ms.module.list.FruitListExample;
import wowjoy.fruits.ms.module.logs.FruitLogsDao;
import wowjoy.fruits.ms.module.relation.entity.TaskListRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskPlanRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskProjectRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskUserRelation;
import wowjoy.fruits.ms.module.task.FruitTask;
import wowjoy.fruits.ms.module.task.FruitTaskDao;
import wowjoy.fruits.ms.module.task.FruitTaskExample;
import wowjoy.fruits.ms.module.task.FruitTaskVo;
import wowjoy.fruits.ms.module.task.mapper.FruitTaskMapper;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

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
    @Autowired
    private FruitTaskMapper taskMapper;
    @Qualifier("taskPlanDaoImpl")
    @Autowired
    private TaskPlanDaoImpl taskPlanDao;
    @Qualifier("taskProjectDaoImpl")
    @Autowired
    private TaskProjectDaoImpl taskProjectDao;
    @Qualifier("taskUserDaoImpl")
    @Autowired
    private TaskUserDaoImpl taskUserDao;
    @Qualifier("taskListDaoImpl")
    @Autowired
    private TaskListDaoImpl taskListDao;
    @Autowired
    private ListDaoImpl listDao;
    @Autowired
    private AbstractDaoLogs logsDaoImpl;
    @Autowired
    private UserDaoImpl userDao;

    @Override
    public void insert(FruitTaskDao dao) {
        /*插入任务*/
        taskMapper.insertSelective(dao);
        Relation.getInstance(dao, taskPlanDao, taskProjectDao, taskUserDao, taskListDao).insertList().insertPlan().insertProject().insertUser();
    }

    @Override
    public List<FruitTaskDao> findByExample(Consumer<FruitTaskExample> consumerExample) {
        FruitTaskExample example = new FruitTaskExample();
        consumerExample.accept(example);
        return taskMapper.selectByExample(example);
    }

    @Override
    public void update(Consumer<FruitTaskDao> daoConsumer, Consumer<FruitTaskExample> taskExampleConsumer) {
        FruitTaskDao dao = FruitTask.getDao();
        FruitTaskExample example = new FruitTaskExample();
        daoConsumer.accept(dao);
        taskExampleConsumer.accept(example);
        taskMapper.updateByExampleSelective(dao, example);
        Relation.getInstance(dao, taskPlanDao, taskProjectDao, taskUserDao, taskListDao)
                /*删除列表、计划、项目、用户的关联信息*/
                .removeList().removePlan().removeProject().removeUser()
                /*添加列表、计划、项目、用户的关联信息*/
                .insertList().insertPlan().insertProject().insertUser();
    }

    @Override
    public void delete(FruitTaskDao dao) {
        if (StringUtils.isBlank(dao.getUuid()))
            throw new CheckException("缺少任务id");
        FruitTaskExample example = new FruitTaskExample();
        FruitTaskExample.Criteria criteria = example.createCriteria();
        criteria.andUuidEqualTo(dao.getUuid());
        FruitTaskDao delete = FruitTask.getDao();
        delete.setIsDeleted(Systems.Y.name());
        taskMapper.updateByExampleSelective(delete, example);
        /*删除项目、计划、用户关联信息*/
        Relation.getInstance(dao, taskPlanDao, taskProjectDao, taskUserDao, taskListDao)
                .removeLists().removePlans().removeProjects().removeUsers();
    }

    @Override
    public List<FruitTaskDao> findByListExampleAndProjectId(Consumer<FruitTaskExample> exampleUnaryOperator, Consumer<FruitListExample> listExampleConsumer, String projectId) {
        if (projectId == null || projectId.isEmpty()) return Lists.newLinkedList();
        FruitTaskExample example = new FruitTaskExample();
        FruitListExample listExample = new FruitListExample();
        exampleUnaryOperator.accept(example);
        listExampleConsumer.accept(listExample);
        example.setOrderByClause("task.task_status desc,task.create_date_time desc");
        return taskMapper.selectByListExampleAndProjectId(example, listExample, projectId);
    }

    @Override
    public List<FruitListDao> findProjectList(String projectId, Consumer<FruitListExample> listExampleConsumer) {
        return listDao.findByProjectId(projectId, listExampleConsumer::accept);
    }

    @Override
    public List<FruitTaskDao> findJoinUserByTaskIds(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty())
            return Lists.newLinkedList();
        return taskMapper.selectJoinUserByTaskIds(taskIds);
    }

    @Override
    public List<FruitTaskDao> findPlanByTaskIds(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty())
            return Lists.newLinkedList();
        FruitTaskExample example = new FruitTaskExample();
        example.createCriteria().andUuidIn(taskIds).andIsDeletedEqualTo(Systems.N.name());
        return taskMapper.selectPlanByTask(example);
    }

    @Override
    public List<FruitTaskDao> findProjectByTask(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty())
            return Lists.newLinkedList();
        FruitTaskExample example = new FruitTaskExample();
        example.createCriteria().andUuidIn(taskIds).andIsDeletedEqualTo(Systems.N.name());
        return taskMapper.selectProjectByTask(example);
    }

    @Override
    public List<FruitTaskDao> findPlanJoinProjectByTask(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty())
            return Lists.newLinkedList();
        return taskMapper.selectPlanJoinProjectByTask(taskIds);
    }

    @Override
    public List<FruitTaskDao> findListByTask(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty())
            return Lists.newLinkedList();
        FruitTaskExample example = new FruitTaskExample();
        example.createCriteria().andUuidIn(taskIds).andIsDeletedEqualTo(Systems.N.name());
        return taskMapper.selectListByTask(example);
    }

    @Override
    public Map<String, LinkedList<FruitLogsDao>> findJoinLogsByTask(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) return Maps.newLinkedHashMap();
        return logsDaoImpl.findLogs(example -> {
            example.createCriteria().andFruitUuidIn(taskIds).andFruitTypeEqualTo(FruitDict.Parents.TASK.name());
            example.setOrderByClause("flogs.create_date_time desc");
        }, (logsDao, template) -> {
            if (logsDao.getOperateType() != FruitDict.LogsDict.HANDOVER)
                return template;
            FruitTaskVo vo = new Gson().fromJson(logsDao.getVoObject(), TypeToken.of(FruitTaskVo.class).getType());
            if (vo == null || vo.getUserRelation() == null) return template;
            Predicate<Systems> userPredicate = key -> vo.getUserRelation().containsKey(key) && !vo.getUserRelation().get(key).isEmpty();
            Function<List<TaskUserRelation>, String> userFunction = userRelations -> userDao.findExample(example -> example.createCriteria().andUserIdIn(
                    userRelations
                            .stream()
                            .map(TaskUserRelation::getUserId)
                            .collect(toList())
            )).stream().map(FruitUserDao::getUserName).reduce((l, r) -> l + "、" + r).get();
            LinkedList<Object> appends = Lists.newLinkedList();
            if (userPredicate.test(Systems.ADD))
                appends.add(userFunction.andThen(text -> "添加成员：" + text).apply(vo.getUserRelation().get(Systems.ADD)));
            if (userPredicate.test(Systems.DELETE))
                appends.add(userFunction.andThen(text -> "移除成员：" + text).apply(vo.getUserRelation().get(Systems.DELETE)));
            return template + "，" + appends.stream().reduce((l, r) -> l + "，" + r).get();
        }, FruitDict.Parents.TASK);
    }

    public List<FruitTaskDao> findByExampleAndUserIdByProjectId(FruitTaskExample example, String projectId, List<String> userIds) {
        return taskMapper.selectByExampleAndUserIdAndProjectId(example, projectId, userIds);
    }


    /**
     * 任务关联信息管理
     */
    private static class Relation {
        private final FruitTaskDao dao;
        private final TaskPlanDaoImpl planDao;
        private final TaskProjectDaoImpl projectDao;
        private final TaskUserDaoImpl userDao;
        private final TaskListDaoImpl listDao;

        private Relation(FruitTaskDao dao, TaskPlanDaoImpl planDao, TaskProjectDaoImpl projectDao, TaskUserDaoImpl userDao, TaskListDaoImpl listDao) {
            this.dao = dao;
            this.planDao = planDao;
            this.projectDao = projectDao;
            this.userDao = userDao;
            this.listDao = listDao;
        }

        static Relation getInstance(FruitTaskDao dao, TaskPlanDaoImpl planDao, TaskProjectDaoImpl projectDao, TaskUserDaoImpl userDao, TaskListDaoImpl listDao) {
            return new Relation(dao, planDao, projectDao, userDao, listDao);
        }

        Relation insertPlan() {
            dao.getPlanRelation(Systems.ADD).forEach((i) -> {
                i.setTaskId(dao.getUuid());
                planDao.insert(i);
            });
            return this;
        }

        Relation insertProject() {
            dao.getProjectRelation(Systems.ADD).forEach((i) -> {
                i.setTaskId(dao.getUuid());
                projectDao.insert(i);
            });
            return this;
        }

        Relation insertUser() {
            dao.getUserRelation(Systems.ADD).forEach((i) -> {
                i.setTaskId(dao.getUuid());
                i.setUserRole(FruitDict.TaskUserDict.EXECUTOR);
                userDao.insert(i);
            });
            return this;
        }

        Relation insertList() {
            if (dao.getListRelation(Systems.ADD).isEmpty()) return this;
            dao.getListRelation(Systems.ADD).forEach((i) -> {
                i.setTaskId(dao.getUuid());
                listDao.insert(i);
            });
            return this;
        }

        Relation removeProjects() {
            projectDao.deleted(TaskProjectRelation.newInstance(dao.getUuid(), null));
            return this;
        }

        Relation removeProject() {
            dao.getProjectRelation(Systems.DELETE).forEach((i) -> projectDao.deleted(TaskProjectRelation.newInstance(dao.getUuid(), i.getProjectId())));
            return this;
        }

        Relation removePlans() {
            planDao.deleted(TaskPlanRelation.newInstance(dao.getUuid(), null));
            return this;
        }

        Relation removePlan() {
            dao.getPlanRelation(Systems.DELETE).forEach((i) -> planDao.deleted(TaskPlanRelation.newInstance(dao.getUuid(), i.getPlanId())));
            return this;
        }

        Relation removeUsers() {
            userDao.deleted(TaskUserRelation.newInstance(dao.getUuid(), null));
            return this;
        }

        Relation removeUser() {
            dao.getUserRelation(Systems.DELETE).forEach((i) -> userDao.deleted(TaskUserRelation.newInstance(dao.getUuid(), i.getUserId())));
            return this;
        }

        Relation removeLists() {
            listDao.deleted(TaskListRelation.newInstance(dao.getUuid(), null));
            return this;
        }

        Relation removeList() {
            if (dao.getListRelation(Systems.DELETE).isEmpty()) return this;
            dao.getListRelation(Systems.DELETE).forEach((i) -> listDao.deleted(TaskListRelation.newInstance(dao.getUuid(), i.getListId())));
            return this;
        }

    }

    /************************************************************************************************
     *                                       个人中心专供                                            *
     ************************************************************************************************/

    @Override
    protected List<FruitTaskDao> myTask(FruitTaskDao dao) {
        String projectId = dao.getProjectIds() != null && !dao.getProjectIds().isEmpty() ? dao.getProjectIds().get(0) : null;
//        PageHelper.startPage(dao.getPageNum(), dao.getPageSize());
        return taskMapper.myTaskByExample(myTaskTemplate(dao), ApplicationContextUtils.getCurrentUser().getUserId(), projectId);
    }

    @Override
    protected List<FruitTaskDao> myCreateTask(FruitTaskDao dao) {
        return taskMapper.myCreateTask(myTaskTemplate(dao), ApplicationContextUtils.getCurrentUser().getUserId());
    }

    private FruitTaskExample myTaskTemplate(FruitTaskDao dao) {
        final String prefix = "task.";
        FruitTaskExample example = new FruitTaskExample();
        FruitTaskExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getTitle()))
            criteria.andTitleLike(MessageFormat.format("%{0}%", dao.getTitle()));
        if (StringUtils.isNotBlank(dao.getTaskStatus()))
            criteria.andTaskStatusEqualTo(dao.getTaskStatus());
        String sort = dao.sortConstrue(prefix);
        if (StringUtils.isBlank(sort))
            sort = MessageFormat.format("{0}create_date_time desc", prefix);
        example.setOrderByClause(sort);
        criteria.andIsDeletedEqualTo(Systems.N.name());
        return example;
    }

}
