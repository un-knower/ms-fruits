package wowjoy.fruits.ms.dao.task;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.list.ListDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.TaskListDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.TaskPlanDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.TaskProjectDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.TaskUserDaoImpl;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.list.FruitListDao;
import wowjoy.fruits.ms.module.relation.entity.TaskListRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskPlanRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskProjectRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskUserRelation;
import wowjoy.fruits.ms.module.task.FruitTask;
import wowjoy.fruits.ms.module.task.FruitTaskDao;
import wowjoy.fruits.ms.module.task.FruitTaskExample;
import wowjoy.fruits.ms.module.task.mapper.FruitTaskMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.text.MessageFormat;
import java.util.List;

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

    @Override
    public void insert(FruitTaskDao dao) {
        /*插入任务*/
        taskMapper.insertSelective(dao);
        Relation.getInstance(dao, taskPlanDao, taskProjectDao, taskUserDao, taskListDao).insertList().insertPlan().insertProject().insertUser();
    }

    @Override
    public List<FruitTaskDao> finds(FruitTaskDao dao) {
        FruitTaskExample example = new FruitTaskExample();
        FruitTaskExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        if (StringUtils.isNotBlank(dao.getTitle()))
            criteria.andTitleEqualTo(MessageFormat.format("%{0}%", dao.getTitle()));
        return taskMapper.selectByExample(example);
    }

    @Override
    protected FruitTask find(FruitTaskDao dao) {
        FruitTaskExample example = new FruitTaskExample();
        FruitTaskExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        List<FruitTaskDao> datas = taskMapper.selectByExample(example);
        if (datas.isEmpty())
            return FruitTask.getEmpty();
        return datas.get(0);
    }

    @Override
    public void update(FruitTaskDao dao) {
        FruitTaskExample example = new FruitTaskExample();
        FruitTaskExample.Criteria criteria = example.createCriteria();
        criteria.andUuidEqualTo(dao.getUuid());
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
        delete.setIsDeleted(FruitDict.Systems.Y.name());
        taskMapper.updateByExampleSelective(delete, example);
        /*删除项目、计划、用户关联信息*/
        Relation.getInstance(dao, taskPlanDao, taskProjectDao, taskUserDao, taskListDao)
                .removeLists().removePlans().removeProjects().removeUsers();
    }

    @Override
    protected List<FruitTaskDao> findByPlanId(FruitTaskDao dao) {
        PageHelper.startPage(dao.getPageNum(), dao.getPageSize());
        return taskMapper.selectByTaskPlan(dao.getPlanId());
    }

    @Override
    public List<FruitTaskDao> findByListId(FruitTaskDao dao) {
        final String prefix = "task.";
        final String status = prefix + "task_status desc";
        PageHelper.startPage(dao.getPageNum(), dao.getPageSize());
        FruitTaskExample example = new FruitTaskExample();
        StringBuffer sort = new StringBuffer();
        String sortConstrue = dao.sortConstrue(prefix);
        sort.append(StringUtils.isNotBlank(sortConstrue) ? sortConstrue : "");
        if (StringUtils.isBlank(sort.toString()))
            sort.append(status).append(",").append("task.create_date_time desc");
        else
            sort.insert(0, status).append(",");
        example.setOrderByClause(sort.toString());
        List<FruitTaskDao> data = taskMapper.selectByTaskList(example, dao.getListId());
        return data;
    }

    @Override
    protected List<FruitListDao> findProjectList(List<String> projectId) {
        return listDao.findByProjectId(projectId);
    }

    @Override
    protected List<FruitTaskDao> findUserByTaskIds(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty())
            return Lists.newLinkedList();
        FruitTaskExample example = new FruitTaskExample();
        example.createCriteria().andUuidIn(taskIds).andIsDeletedEqualTo(FruitDict.Systems.N.name());
        List<FruitTaskDao> data = taskMapper.selectUserByTask(example);
        return data;
    }

    @Override
    protected List<FruitTaskDao> findPlanByTaskIds(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty())
            return Lists.newLinkedList();
        FruitTaskExample example = new FruitTaskExample();
        example.createCriteria().andUuidIn(taskIds).andIsDeletedEqualTo(FruitDict.Systems.N.name());
        List<FruitTaskDao> data = taskMapper.selectPlanByTask(example);
        return data;
    }

    @Override
    protected List<FruitTaskDao> findProjectByTask(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty())
            return Lists.newLinkedList();
        FruitTaskExample example = new FruitTaskExample();
        example.createCriteria().andUuidIn(taskIds).andIsDeletedEqualTo(FruitDict.Systems.N.name());
        List<FruitTaskDao> data = taskMapper.selectProjectByTask(example);
        return data;
    }

    @Override
    protected List<FruitTaskDao> findPlanJoinProjectByTask(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty())
            return Lists.newLinkedList();
        FruitTaskExample example = new FruitTaskExample();
        example.createCriteria().andUuidIn(taskIds).andIsDeletedEqualTo(FruitDict.Systems.N.name());
        List<FruitTaskDao> data = taskMapper.selectPlanJoinProjectByTask(example);
        return data;
    }

    @Override
    protected List<FruitTaskDao> findListByTask(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty())
            return Lists.newLinkedList();
        FruitTaskExample example = new FruitTaskExample();
        example.createCriteria().andUuidIn(taskIds).andIsDeletedEqualTo(FruitDict.Systems.N.name());
        List<FruitTaskDao> data = taskMapper.selectListByTask(example);
        return data;
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

        public static Relation getInstance(FruitTaskDao dao, TaskPlanDaoImpl planDao, TaskProjectDaoImpl projectDao, TaskUserDaoImpl userDao, TaskListDaoImpl listDao) {
            return new Relation(dao, planDao, projectDao, userDao, listDao);
        }

        public Relation insertPlan() {
            dao.getPlanRelation(FruitDict.Systems.ADD).forEach((i) -> {
                i.setTaskId(dao.getUuid());
                planDao.insert(i);
            });
            return this;
        }

        public Relation insertProject() {
            dao.getProjectRelation(FruitDict.Systems.ADD).forEach((i) -> {
                i.setTaskId(dao.getUuid());
                projectDao.insert(i);
            });
            return this;
        }

        public Relation insertUser() {
            dao.getUserRelation(FruitDict.Systems.ADD).forEach((i) -> {
                i.setTaskId(dao.getUuid());
                i.setUserRole(FruitDict.TaskUserDict.EXECUTOR);
                userDao.insert(i);
            });
            return this;
        }

        public Relation insertList() {
            if (dao.getListRelation(FruitDict.Systems.ADD).isEmpty()) return this;
            dao.getListRelation(FruitDict.Systems.ADD).forEach((i) -> {
                i.setTaskId(dao.getUuid());
                listDao.insert(i);
            });
            return this;
        }

        public Relation removeProjects() {
            projectDao.deleted(TaskProjectRelation.newInstance(dao.getUuid(), null));
            return this;
        }

        public Relation removeProject() {
            dao.getProjectRelation(FruitDict.Systems.DELETE).forEach((i) -> projectDao.deleted(TaskProjectRelation.newInstance(dao.getUuid(), i.getProjectId())));
            return this;
        }

        public Relation removePlans() {
            planDao.deleted(TaskPlanRelation.newInstance(dao.getUuid(), null));
            return this;
        }

        public Relation removePlan() {
            dao.getPlanRelation(FruitDict.Systems.DELETE).forEach((i) -> planDao.deleted(TaskPlanRelation.newInstance(dao.getUuid(), i.getPlanId())));
            return this;
        }

        public Relation removeUsers() {
            userDao.deleted(TaskUserRelation.newInstance(dao.getUuid(), null));
            return this;
        }

        public Relation removeUser() {
            dao.getUserRelation(FruitDict.Systems.DELETE).forEach((i) -> userDao.deleted(TaskUserRelation.newInstance(dao.getUuid(), i.getUserId())));
            return this;
        }

        public Relation removeLists() {
            listDao.deleted(TaskListRelation.newInstance(dao.getUuid(), null));
            return this;
        }

        public Relation removeList() {
            if (dao.getListRelation(FruitDict.Systems.DELETE).isEmpty()) return this;
            dao.getListRelation(FruitDict.Systems.DELETE).forEach((i) -> listDao.deleted(TaskListRelation.newInstance(dao.getUuid(), i.getListId())));
            return this;
        }

    }

    /************************************************************************************************
     *                                       个人中心专供                                            *
     ************************************************************************************************/

    @Override
    protected List<FruitTaskDao> myTask(FruitTaskDao dao) {
        final String prefix = "task.";
        FruitTaskExample example = new FruitTaskExample();
        FruitTaskExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getTitle()))
            criteria.andTitleLike(MessageFormat.format("%{0}%", dao.getTitle()));
        if (StringUtils.isNotBlank(dao.getTaskStatus()))
            criteria.andTaskStatusEqualTo(dao.getTaskStatus());
        String projectId = dao.getProjectIds()!=null && !dao.getProjectIds().isEmpty() ? dao.getProjectIds().get(0) : null;
        String sort = dao.sortConstrue(prefix);
        if (StringUtils.isBlank(sort))
            sort = MessageFormat.format("{0}create_date_time desc", prefix);
        example.setOrderByClause(sort);
        criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name());
//        PageHelper.startPage(dao.getPageNum(), dao.getPageSize());
        return taskMapper.myTaskByExample(example, ApplicationContextUtils.getCurrentUser().getUserId(), projectId);
    }
}
