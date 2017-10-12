package wowjoy.fruits.ms.dao.task;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.impl.TaskPlanDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.TaskProjectDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.TaskUserDaoImpl;
import wowjoy.fruits.ms.module.relation.entity.TaskPlanRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskProjectRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskUserRelation;
import wowjoy.fruits.ms.module.task.FruitTaskDao;
import wowjoy.fruits.ms.module.task.FruitTaskExample;
import wowjoy.fruits.ms.module.task.mapper.FruitTaskMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

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
    private TaskPlanDaoImpl planDao;
    @Qualifier("taskProjectDaoImpl")
    @Autowired
    private TaskProjectDaoImpl projectDao;
    @Qualifier("taskUserDaoImpl")
    @Autowired
    private TaskUserDaoImpl userDao;


    @Override
    public void insert(FruitTaskDao dao) {
        /*插入任务*/
        taskMapper.insertSelective(dao);
        Relation.getInstance(dao, planDao, projectDao, userDao).insertPlan().insertProject().insertUser();
    }

    @Override
    protected List<FruitTaskDao> finds(FruitTaskDao dao) {
        FruitTaskExample example = new FruitTaskExample();
        FruitTaskExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        return taskMapper.selectByExample(example);
    }

    @Override
    protected void update(FruitTaskDao dao) {
        FruitTaskExample example = new FruitTaskExample();
        FruitTaskExample.Criteria criteria = example.createCriteria();
        criteria.andUuidEqualTo(dao.getUuid());
        taskMapper.updateByExampleSelective(dao, example);
        Relation.getInstance(dao, planDao, projectDao, userDao)
                /*删除计划、项目、用户的关联信息*/
                .removePlan().removeProject().removeUser()
                /*添加计划、项目、用户的关联信息*/
                .insertPlan().insertProject().insertUser();

    }

    @Override
    protected void delete(FruitTaskDao dao) {
        FruitTaskExample example = new FruitTaskExample();
        FruitTaskExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        taskMapper.deleteByExample(example);
        /*删除项目、计划、用户关联信息*/
        Relation.getInstance(dao, planDao, projectDao, userDao).removePlans().removeProjects().removeUsers();
    }

    @Override
    protected List<TaskPlanRelation> findJoinPlan(TaskPlanRelation relation) {
        return planDao.finds(relation);
    }

    @Override
    protected List<TaskProjectRelation> findJoinProject(TaskProjectRelation relation) {
        return projectDao.finds(relation);
    }

    /**
     * 任务关联信息管理
     */
    private static class Relation {
        private final FruitTaskDao dao;
        private final TaskPlanDaoImpl planDao;
        private final TaskProjectDaoImpl projectDao;
        private final TaskUserDaoImpl userDao;

        private Relation(FruitTaskDao dao, TaskPlanDaoImpl planDao, TaskProjectDaoImpl projectDao, TaskUserDaoImpl userDao) {
            this.dao = dao;
            this.planDao = planDao;
            this.projectDao = projectDao;
            this.userDao = userDao;
        }

        public static Relation getInstance(FruitTaskDao dao, TaskPlanDaoImpl planDao, TaskProjectDaoImpl projectDao, TaskUserDaoImpl userDao) {
            return new Relation(dao, planDao, projectDao, userDao);
        }

        public Relation insertPlan() {
            dao.getTaskPlanRelation(FruitDict.Dict.ADD).forEach((i) -> {
                i.setTaskId(dao.getUuid());
                planDao.insert(i);
            });
            return this;
        }

        public Relation insertProject() {
            dao.getTaskProjectRelation(FruitDict.Dict.ADD).forEach((i) -> {
                i.setTaskId(dao.getUuid());
                projectDao.insert(i);
            });
            return this;
        }

        public Relation insertUser() {
            dao.getTaskUserRelation(FruitDict.Dict.ADD).forEach((i) -> {
                i.setTaskId(dao.getUuid());
                userDao.insert(i);
            });
            return this;
        }

        public Relation removeProjects() {
            projectDao.remove(TaskProjectRelation.newInstance(dao.getUuid(), null));
            return this;
        }

        public Relation removeProject() {
            dao.getTaskProjectRelation(FruitDict.Dict.DELETE).forEach((i) -> {
                projectDao.remove(TaskProjectRelation.newInstance(dao.getUuid(), i.getProjectId()));
            });
            return this;
        }

        public Relation removePlans() {
            planDao.remove(TaskPlanRelation.newInstance(dao.getUuid(), null));
            return this;
        }

        public Relation removePlan() {
            dao.getTaskPlanRelation(FruitDict.Dict.DELETE).forEach((i) -> {
                planDao.remove(TaskPlanRelation.newInstance(dao.getUuid(), i.getPlanId()));
            });
            return this;
        }

        public void removeUsers() {
            userDao.remove(TaskUserRelation.newInstance(dao.getUuid(), null));
        }

        public Relation removeUser() {
            dao.getTaskUserRelation(FruitDict.Dict.DELETE).forEach((i) -> {
                userDao.remove(TaskUserRelation.newInstance(dao.getUuid(), i.getUserId()));
            });
            return this;
        }

    }
}
