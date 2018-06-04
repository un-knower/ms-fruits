package wowjoy.fruits.ms.dao.project;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.list.ListDaoImpl;
import wowjoy.fruits.ms.dao.plan.PlanDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.ProjectTeamDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.UserProjectDaoImpl;
import wowjoy.fruits.ms.dao.task.AbstractDaoTask;
import wowjoy.fruits.ms.dao.task.TaskDaoImpl;
import wowjoy.fruits.ms.dao.team.AbstractDaoTeam;
import wowjoy.fruits.ms.dao.team.TeamDaoImpl;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.list.FruitList;
import wowjoy.fruits.ms.module.mark.UserMarkProject;
import wowjoy.fruits.ms.module.mark.UserMarkProjectExample;
import wowjoy.fruits.ms.module.mark.mapper.UserMarkProjectMapper;
import wowjoy.fruits.ms.module.plan.FruitPlanUser;
import wowjoy.fruits.ms.module.plan.example.FruitPlanExample;
import wowjoy.fruits.ms.module.project.*;
import wowjoy.fruits.ms.module.project.mapper.FruitProjectMapper;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.task.FruitTaskExample;
import wowjoy.fruits.ms.module.task.FruitTaskProject;
import wowjoy.fruits.ms.module.task.FruitTaskUser;
import wowjoy.fruits.ms.module.team.FruitTeamUser;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by wangziwen on 2017/9/6.
 */

@Service
@Transactional
public class ProjectDaoImpl extends AbstractDaoProject {

    private final FruitProjectMapper projectMapper;
    private final ProjectTeamDaoImpl teamRelationDao;
    private final UserProjectDaoImpl userRelationDao;
    private final PlanDaoImpl planDaoImpl;
    private final AbstractDaoTask taskDaoImpl;
    private final ListDaoImpl listDao;
    private final AbstractDaoTeam teamDao;
    private final TaskDaoImpl taskDao;
    private final UserMarkProjectMapper markProjectMapper;

    @Autowired
    public ProjectDaoImpl(TaskDaoImpl taskDao, TeamDaoImpl teamDao,
                          FruitProjectMapper projectMapper,
                          @Qualifier("projectTeamDaoImpl") ProjectTeamDaoImpl teamRelationDao,
                          @Qualifier("userProjectDaoImpl") UserProjectDaoImpl userRelationDao,
                          PlanDaoImpl planDaoImpl, TaskDaoImpl taskDaoImpl, ListDaoImpl listDao,
                          UserMarkProjectMapper markProjectMapper) {
        this.teamDao = teamDao;
        this.projectMapper = projectMapper;
        this.teamRelationDao = teamRelationDao;
        this.userRelationDao = userRelationDao;
        this.planDaoImpl = planDaoImpl;
        this.taskDaoImpl = taskDaoImpl;
        this.listDao = listDao;
        this.taskDao = taskDao;
        this.markProjectMapper = markProjectMapper;
    }

    @Override
    public List<FruitTeamUser> findUserByTeamId(ArrayList<String> teamIds) {
        return teamDao.findUserByTeamIds(teamIds, example -> example.createCriteria()
                .andIsDeletedEqualTo(Systems.N.name()));
    }

    @Override
    public List<FruitList> findListByProjectId(String projectId) {
        return listDao.findByProjectId(projectId, listExample -> listExample.createCriteria().andIsDeletedEqualTo(Systems.N.name()));
    }

    @Override
    public void insert(Consumer<FruitProjectDao> daoConsumer) {
        FruitProjectDao dao = FruitProject.getDao();
        daoConsumer.accept(dao);
        projectMapper.insertSelective(dao);
        Relation.getInstance(teamRelationDao, userRelationDao, dao).insertTeamRelation().insertUserRelation();
    }

    @Override
    public List<FruitProject> finds(Consumer<FruitProjectExample> exampleConsumer) {
        FruitProjectExample example = new FruitProjectExample();
        exampleConsumer.accept(example);
        return projectMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public ArrayList<FruitProjectUser> findUserByProjectIdsAndRole(List<String> ids, ArrayList<FruitDict.UserProjectDict> roles) {
        if (ids.isEmpty())
            throw new CheckException("查询关联用户时，必须提供项目id");
        return projectMapper.selectUserByProjectIdAndRole(ids, roles);
    }

    @Override
    public List<FruitProjectTeam> findTeamByProjectIds(List<String> ids) {
        if (ids.isEmpty())
            throw new CheckException("查询关联团队时，必须提供项目id");
        return projectMapper.selectTeamByProjectId(ids);
    }

    @Override
    public void update(Consumer<FruitProjectDao> daoConsumer, Consumer<FruitProjectExample> exampleConsumer) {
        FruitProjectDao dao = FruitProject.getDao();
        daoConsumer.accept(dao);
        /*修改项目信息*/
        FruitProjectExample example = new FruitProjectExample();
        exampleConsumer.accept(example);
        projectMapper.updateByExampleSelective(dao, example);
        /*删除关联*/
        Relation.getInstance(teamRelationDao, userRelationDao, dao).removeUserRelation().removeTeamRelation();
        /*添加关联*/
        Relation.getInstance(teamRelationDao, userRelationDao, dao).insertTeamRelation().insertUserRelation();
    }

    @Override
    public void delete(String uuid) {
        if (StringUtils.isBlank(uuid))
            throw new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name());
        FruitProjectExample example = new FruitProjectExample();
        example.createCriteria().andUuidEqualTo(uuid);
        FruitProjectDao delete = FruitProject.getDao();
        delete.setIsDeleted(Systems.Y.name());
        projectMapper.updateByExampleSelective(delete, example);
        FruitProjectDao projectDao = FruitProject.getDao();
        projectDao.setUuid(uuid);
        Relation.getInstance(teamRelationDao, userRelationDao, projectDao)
                .removesUserRelation().removesTeamRelation();
    }

    @Override
    public List<FruitPlanUser> findPlanByPlanExampleAndUserIdsAnProjectId(Consumer<FruitPlanExample> exampleConsumer, List<String> userIds, String projectId) {
        return planDaoImpl.findUserByPlanExampleAndUserIdOrProjectId(exampleConsumer, projectId, userIds);
    }

    @Override
    public List<FruitTaskUser> findTaskByTaskExampleAndUserIdsAndProjectId(Consumer<FruitTaskExample> exampleConsumer, List<String> userIds, String projectId) {
        return taskDaoImpl.findContainUserListByUserIdByProjectId(exampleConsumer, userIds, projectId);
    }

    @Override
    public List<FruitProjectUser> findAllUserByProjectId(String projectId) {
        return projectMapper.selectAllUserByProjectId(
                Optional.ofNullable(projectId).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name()))
        );
    }

    /****************
     * 当前用户函数
     ****************/

    @Override
    public List<FruitProjectDao> findsCurrentUser(Consumer<FruitProjectExample> exampleConsumer) {
        FruitProjectExample example = new FruitProjectExample();
        exampleConsumer.accept(example);
        return projectMapper.selectByUserIdAndExample(example, ApplicationContextUtils.getCurrentUser().getUserId());
    }

    @Override
    public List<FruitTaskProject> myCreateTaskFromProjects() {
        return taskDao.myCreateTaskFromProject();
    }

    @Override
    protected List<FruitTeamUser> findTeamUserByTeamIds(List<String> teamIds) {
        return teamDao.plugUserSupplier(teamIds, example -> {
        }).get();
    }

    @Override
    protected List<UserMarkProject> findMarkProject(String userId) {
        UserMarkProjectExample example = new UserMarkProjectExample();
        example.createCriteria().andUserIdEqualTo(userId).andIsDeletedEqualTo(Systems.N.name());
        return markProjectMapper.selectByExample(example);
    }

    @Override
    public void star(String projectId) {
        Optional.ofNullable(projectId)
                .filter(StringUtils::isNotBlank)
                .ifPresent(id -> {
                    UserMarkProject.Insert insert = new UserMarkProject.Insert();
                    insert.setProjectId(projectId);
                    insert.setUserId(ApplicationContextUtils.getCurrentUser().getUserId());
                    markProjectMapper.insertSelective(insert);
                });
    }

    @Override
    public void unStar(String projectId) {
        Optional.ofNullable(projectId)
                .ifPresent(id -> {
                    UserMarkProjectExample example = new UserMarkProjectExample();
                    example.createCriteria().andProjectIdEqualTo(projectId).andUserIdEqualTo(ApplicationContextUtils.getCurrentUser().getUserId());
                    UserMarkProject.Update update = new UserMarkProject.Update();
                    update.setIsDeleted(Systems.Y.name());
                    markProjectMapper.updateByExampleSelective(update, example);
                });
    }

    /**
     * 非静态类，对外部类提供关联功能
     * 随着系统迭代，替换掉现有的关联操作实现方式（具体参考目标dao实现）
     */
    @Deprecated
    private static class Relation {
        private final ProjectTeamDaoImpl TeamDao;
        private final UserProjectDaoImpl UserDao;
        private final FruitProjectDao Dao;

        Relation(ProjectTeamDaoImpl teamDao, UserProjectDaoImpl userDao, FruitProjectDao dao) {
            this.TeamDao = teamDao;
            this.UserDao = userDao;
            this.Dao = dao;
        }

        public static Relation getInstance(ProjectTeamDaoImpl teamDao, UserProjectDaoImpl userDao, FruitProjectDao dao) {
            return new Relation(teamDao, userDao, dao);
        }

        /**
         * 删除所有关联用户
         */
        private Relation removesUserRelation() {
            UserDao.deleted(UserProjectRelation.newInstance(Dao.getUuid(), null));
            return this;
        }

        /**
         * 删除指定关联用户
         */
        private Relation removeUserRelation() {
            Dao.getUserRelation(Systems.DELETE).forEach((i) -> {
                if (StringUtils.isBlank(i.getUserId()))
                    throw new CheckException(FruitDict.Exception.Check.PROJECT_USER_NULL.name());
                UserDao.deleted(UserProjectRelation.newInstance(Dao.getUuid(), i.getUserId(), StringUtils.isNotBlank(i.getUpRole()) ? i.getUpRole() : null));
            });
            return this;
        }


        /**
         * 删除所有关联团队
         */
        private Relation removesTeamRelation() {
            TeamDao.deleted(ProjectTeamRelation.newInstance(Dao.getUuid(), null));
            return this;
        }

        /**
         * 删除指定关联团队
         */
        private Relation removeTeamRelation() {
            Dao.getTeamRelation(Systems.DELETE).forEach((i) -> {
                if (StringUtils.isBlank(i.getTeamId()))
                    throw new CheckException(FruitDict.Exception.Check.PROJECT_TEAM_NULL.name());
                TeamDao.deleted(ProjectTeamRelation.newInstance(Dao.getUuid(), i.getTeamId(), StringUtils.isNotBlank(i.getTpRole()) ? i.getTpRole() : null));
            });
            return this;
        }


        /**
         * 添加用户关联
         */
        private Relation insertUserRelation() {
            Dao.getUserRelation(Systems.ADD).forEach((i) -> {
                i.setProjectId(Dao.getUuid());
                UserDao.insert(i);
            });
            return this;
        }

        /**
         * 添加团队关联
         */
        private Relation insertTeamRelation() {
            Dao.getTeamRelation(Systems.ADD).forEach((i) -> {
                i.setProjectId(Dao.getUuid());
                TeamDao.insert(i);
            });
            return this;
        }
    }
}
