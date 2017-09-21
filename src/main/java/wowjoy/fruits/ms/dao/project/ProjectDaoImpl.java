package wowjoy.fruits.ms.dao.project;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.AbstractDaoRelation;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.project.FruitProjectDao;
import wowjoy.fruits.ms.module.project.FruitProjectExample;
import wowjoy.fruits.ms.module.project.mapper.FruitProjectMapper;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;

import java.util.List;

/**
 * Created by wangziwen on 2017/9/6.
 */
@Service
@Transactional
public class ProjectDaoImpl extends AbstractDaoProject {

    @Autowired
    private FruitProjectMapper projectMapper;
    @Qualifier("projectTeamDaoImpl")
    @Autowired
    private AbstractDaoRelation teamDao;
    @Qualifier("userProjectDaoImpl")
    @Autowired
    private AbstractDaoRelation userDao;

    @Override
    protected void insert(FruitProjectDao dao) {
        projectMapper.insertSelective(dao);
        Relation.getInstance(teamDao, userDao, dao).insertTeamRelation().insertUserRelation();
    }

    @Override
    protected List<FruitProjectDao> findRelation(FruitProjectDao dao) {
        FruitProjectExample example = new FruitProjectExample();
        final FruitProjectExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getTitle()))
            criteria.andTitleEqualTo(dao.getTitle());
        if (StringUtils.isNotBlank(dao.getProjectStatus()))
            criteria.andProjectStatusEqualTo(dao.getProjectStatus());
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        return projectMapper.selectUserRelationByExample(example);
    }

    @Override
    protected List<FruitProjectDao> finds(FruitProjectDao dao) {
        FruitProjectExample example = new FruitProjectExample();
        final FruitProjectExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getTitle()))
            criteria.andTitleEqualTo(dao.getTitle());
        if (StringUtils.isNotBlank(dao.getProjectStatus()))
            criteria.andProjectStatusEqualTo(dao.getProjectStatus());
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        return projectMapper.selectByExample(example);
    }

    @Override
    protected FruitProject findByUUID(String uuid) {
        if (StringUtils.isBlank(uuid))
            throw new CheckProjectException("【项目】uuId不能为空");
        FruitProjectExample example = new FruitProjectExample();
        example.createCriteria().andUuidEqualTo(uuid);
        List<FruitProjectDao> data = projectMapper.selectUserRelationByExample(example);
        if (data.isEmpty())
            return FruitProject.newEmpty("项目不存在");
        return data.get(0);
    }


    @Override
    protected void update(FruitProjectDao dao) {
        /*修改项目信息*/
        FruitProjectExample example = new FruitProjectExample();
        example.createCriteria().andUuidEqualTo(dao.getUuid());
        projectMapper.updateByExampleSelective(dao, example);
        /*删除关联*/
        Relation.getInstance(teamDao, userDao, dao).removeUserRelation().removeTeamRelation();
        /*添加关联*/
        Relation.getInstance(teamDao, userDao, dao).insertTeamRelation().insertUserRelation();
    }

    @Override
    protected void updateStatus(FruitProjectDao dao) {
        if (StringUtils.isBlank(dao.getUuid()))
            throw new CheckProjectException("【updateStatus】uuid无效。");
        final FruitProjectExample example = new FruitProjectExample();
        example.createCriteria().andUuidEqualTo(dao.getUuid());
        projectMapper.updateByExampleSelective(dao, example);
    }

    /**
     * 非静态类，对外部类提供关联功能
     */
    private static class Relation {
        private final AbstractDaoRelation TeamDao;
        private final AbstractDaoRelation UserDao;
        private final FruitProjectDao Dao;

        public Relation(AbstractDaoRelation teamDao, AbstractDaoRelation userDao, FruitProjectDao dao) {
            this.TeamDao = teamDao;
            this.UserDao = userDao;
            this.Dao = dao;
        }

        public static Relation getInstance(AbstractDaoRelation teamDao, AbstractDaoRelation userDao, FruitProjectDao dao) {
            return new Relation(teamDao, userDao, dao);
        }

        /**
         * 删除所有关联用户
         */
        private Relation removeUserRelation() {
            UserDao.remove(UserProjectRelation.newInstance(Dao.getUuid(), null));
            return this;
        }

        /**
         * 删除所有关联团队
         */
        private Relation removeTeamRelation() {
            TeamDao.remove(ProjectTeamRelation.newInstance(Dao.getUuid(), null));
            return this;
        }

        /**
         * 添加用户关联
         */
        private Relation insertUserRelation() {
            Dao.getUserRelation().forEach((i) -> {
                i.setProjectId(Dao.getUuid());
                UserDao.insert(i);
            });
            return this;
        }

        /**
         * 添加团队关联
         */
        private Relation insertTeamRelation() {
            Dao.getTeamRelation().forEach((i) -> {
                i.setProjectId(Dao.getUuid());
                TeamDao.insert(i);
            });
            return this;
        }
    }

}
