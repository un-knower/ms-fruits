package wowjoy.fruits.ms.dao.team;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.impl.UserTeamDaoImpl;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.entity.UserTeamRelation;
import wowjoy.fruits.ms.module.team.FruitTeamDao;
import wowjoy.fruits.ms.module.team.FruitTeamExample;
import wowjoy.fruits.ms.module.team.mapper.FruitTeamMapper;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by wangziwen on 2017/9/5.
 */
// TODO: 2017/10/13 团队关联项目时，需要指明在团队在项目中的角色
@Service
@Transactional(rollbackFor = CheckException.class)
public class TeamDaoImpl extends AbstractDaoTeam {
    @Autowired
    private FruitTeamMapper mapper;
    @Autowired
    private UserTeamDaoImpl dao;

    @Override
    public List<FruitTeamDao> finds(FruitTeamDao dao) {
        final FruitTeamExample example = new FruitTeamExample();
        final FruitTeamExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        if (StringUtils.isNotBlank(dao.getTitle()))
            criteria.andTitleEqualTo(dao.getTitle());
        return mapper.selectByExample(example);
    }

    @Override
    public List<FruitTeamDao> findRelaiton(FruitTeamDao dao, FruitUserDao userDao) {
        final FruitTeamExample example = new FruitTeamExample();
        final FruitTeamExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        if (StringUtils.isNotBlank(dao.getTitle()))
            criteria.andTitleLike(MessageFormat.format("%{0}%", dao.getTitle()));
        FruitUserExample exampleUser = new FruitUserExample();
        FruitUserExample.Criteria criteriaUser = exampleUser.createCriteria();
        if (StringUtils.isNotBlank(userDao.getUserName()))
            criteriaUser.andUserNameLike(MessageFormat.format("%{0}%", userDao.getUserName()));
        return mapper.selectRelationByExample(example, exampleUser);
    }

    @Override
    public void insert(FruitTeamDao data) {
        mapper.insertSelective(data);
        Relation.getInstance(data, dao).insertUsers();
    }

    @Override
    public void update(FruitTeamDao data) {
        if (StringUtils.isBlank(data.getUuid()))
            throw new CheckException("Team id not is null");
        FruitTeamExample example = new FruitTeamExample();
        example.createCriteria().andUuidEqualTo(data.getUuid());
        mapper.updateByExampleSelective(data, example);
        Relation.getInstance(data, dao).deleteUser().insertUsers();
    }

    @Override
    public void delete(FruitTeamDao data) {
        if (StringUtils.isBlank(data.getUuid()))
            throw new CheckException("Team id not is null");
        FruitTeamExample example = new FruitTeamExample();
        example.createCriteria().andUuidEqualTo(data.getUuid());
        mapper.deleteByExample(example);
        Relation.getInstance(data, dao).deleteUsers();
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
            data.getInUsers(FruitDict.Systems.ADD).forEach((i) -> {
                i.setTeamId(data.getUuid());
                dao.insert(i);
            });
        }

        public Relation deleteUser() {
            data.getInUsers(FruitDict.Systems.DELETE).forEach((i) -> dao.remove(UserTeamRelation.newInstance(i.getUserId(), data.getUuid())));
            return this;
        }

        public void deleteUsers() {
            dao.remove(UserTeamRelation.newInstance(data.getUuid()));
        }

    }
}
