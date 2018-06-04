package wowjoy.fruits.ms.dao.team;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.plan.PlanDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.UserTeamDaoImpl;
import wowjoy.fruits.ms.dao.task.TaskDaoImpl;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.plan.FruitPlanUser;
import wowjoy.fruits.ms.module.plan.example.FruitPlanExample;
import wowjoy.fruits.ms.module.relation.entity.UserTeamRelation;
import wowjoy.fruits.ms.module.relation.example.UserTeamRelationExample;
import wowjoy.fruits.ms.module.task.FruitTaskExample;
import wowjoy.fruits.ms.module.task.FruitTaskUser;
import wowjoy.fruits.ms.module.team.FruitTeam;
import wowjoy.fruits.ms.module.team.FruitTeamDao;
import wowjoy.fruits.ms.module.team.FruitTeamExample;
import wowjoy.fruits.ms.module.team.FruitTeamUser;
import wowjoy.fruits.ms.module.team.mapper.FruitTeamMapper;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Exception.Check;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by wangziwen on 2017/9/5.
 */
// TODO: 2017/10/13 团队关联项目时，需要指明在团队在项目中的角色
@Service
@Transactional
public class TeamDaoImpl extends AbstractDaoTeam {
    private final FruitTeamMapper mapper;
    private final UserTeamDaoImpl<UserTeamRelation, UserTeamRelationExample> dao;
    private final PlanDaoImpl planDao;
    private final TaskDaoImpl taskDao;

    @Autowired
    public TeamDaoImpl(FruitTeamMapper mapper, UserTeamDaoImpl<UserTeamRelation, UserTeamRelationExample> dao, PlanDaoImpl planDao, TaskDaoImpl taskDao) {
        this.mapper = mapper;
        this.dao = dao;
        this.planDao = planDao;
        this.taskDao = taskDao;
    }

    @Override
    public List<FruitTeamUser> findUserByTeamIds(List<String> teamIds, Consumer<FruitUserExample> userExampleConsumer) {
        if (teamIds == null || teamIds.isEmpty()) return Lists.newLinkedList();
        FruitUserExample userExample = new FruitUserExample();
        userExampleConsumer.accept(userExample);
        return mapper.selectUserByTeamId(userExample, teamIds);
    }


    @Override
    public List<FruitTeamDao> findTeamByExample(Consumer<FruitTeamExample> teamExampleConsumer) {
        final FruitTeamExample example = new FruitTeamExample();
        teamExampleConsumer.accept(example);
        return mapper.selectByExample(example);
    }

    @Override
    public List<UserTeamRelation> findJoinTeamByUserId(String userId) {
        return dao.finds(example -> {
            UserTeamRelationExample.Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(userId))
                criteria.andUserIdEqualTo(userId);
            criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name());
        });
    }

    @Override
    public void insert(FruitTeamDao data) {
        mapper.insertSelective(data);
        Relation.getInstance(data, dao).insertUsers();
    }

    @Override
    public void update(FruitTeamDao data) {
        if (StringUtils.isBlank(data.getUuid()))
            throw new CheckException(Check.SYSTEM_NULL.name());
        FruitTeamExample example = new FruitTeamExample();
        example.createCriteria().andUuidEqualTo(data.getUuid());
        mapper.updateByExampleSelective(data, example);
        Relation.getInstance(data, dao).deleteUser().insertUsers();
    }

    @Override
    public void delete(FruitTeamDao data) {
        if (StringUtils.isBlank(data.getUuid()))
            throw new CheckException(Check.SYSTEM_NULL.name());
        FruitTeamExample example = new FruitTeamExample();
        example.createCriteria().andUuidEqualTo(data.getUuid());
        FruitTeamDao delete = FruitTeam.getDao();
        delete.setIsDeleted(FruitDict.Systems.Y.name());
        mapper.updateByExampleSelective(delete, example);
        Relation.getInstance(data, this.dao).deleteUsers();
    }

    @Override
    public List<FruitPlanUser> findUserByPlanExampleAndUserId(Consumer<FruitPlanExample> exampleConsumer, ArrayList<String> userIds) {
        return planDao.findUserByPlanExampleAndUserIdOrProjectId(exampleConsumer, null, userIds);
    }

    @Override
    public List<FruitTaskUser> findUserByTaskExampleAndUserId(Consumer<FruitTaskExample> exampleConsumer, ArrayList<String> userIds){
        return taskDao.findUserByTaskExampleAndUserIdOrProjectId(exampleConsumer,userIds,null);
    }

    private static class Relation {
        private FruitTeamDao data;
        private UserTeamDaoImpl dao;

        Relation(FruitTeamDao data, UserTeamDaoImpl dao) {
            this.data = data;
            this.dao = dao;
        }

        public static Relation getInstance(FruitTeamDao dao, UserTeamDaoImpl relation) {
            return new Relation(dao, relation);
        }

        public void insertUsers() {
            data.getUserRelation(FruitDict.Systems.ADD).forEach((i) -> {
                i.setTeamId(data.getUuid());
                dao.insert(i);
            });
        }

        public Relation deleteUser() {
            data.getUserRelation(FruitDict.Systems.DELETE).forEach((i) -> dao.deleted(UserTeamRelation.newInstance(i.getUserId(), data.getUuid(), StringUtils.isNotBlank(i.getUtRole()) ? i.getUtRole() : null)));
            return this;
        }

        public void deleteUsers() {
            dao.deleted(UserTeamRelation.newInstance(data.getUuid()));
        }

    }
}
