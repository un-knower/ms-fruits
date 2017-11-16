package wowjoy.fruits.ms.dao.project;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.impl.ProjectListDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.ProjectTeamDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.UserProjectDaoImpl;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.project.FruitProjectDao;
import wowjoy.fruits.ms.module.project.FruitProjectExample;
import wowjoy.fruits.ms.module.project.mapper.FruitProjectMapper;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.text.MessageFormat;
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
    private ProjectTeamDaoImpl teamDao;
    @Qualifier("userProjectDaoImpl")
    @Autowired
    private UserProjectDaoImpl userDao;


    @Override
    public void insert(FruitProjectDao dao) {
        projectMapper.insertSelective(dao);
        Relation.getInstance(teamDao, userDao, dao).insertTeamRelation().insertUserRelation();
    }

    @Override
    public List<FruitProjectDao> findRelation(FruitProjectDao dao) {
        FruitProjectExample example = new FruitProjectExample();
        final FruitProjectExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getTitle()))
            criteria.andTitleLike(MessageFormat.format("%{0}%", dao.getTitle()));
        if (StringUtils.isNotBlank(dao.getProjectStatus()))
            criteria.andProjectStatusEqualTo(dao.getProjectStatus());
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        example.setOrderByClause("create_date_time desc");
        return projectMapper.selectUserRelationByExample(example);
    }


    @Override
    public List<FruitProjectDao> finds(FruitProjectDao dao) {
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
    public FruitProjectDao findByUUID(String uuid) {
        if (StringUtils.isBlank(uuid))
            throw new CheckException("【项目】uuId不能为空");
        FruitProjectExample example = new FruitProjectExample();
        example.createCriteria().andUuidEqualTo(uuid);
        List<FruitProjectDao> data = projectMapper.selectUserRelationByExample(example);
        return data.get(0);
    }


    @Override
    public void update(FruitProjectDao dao) {
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
    public void delete(String uuid) {
        if (StringUtils.isBlank(uuid))
            throw new CheckException("【项目】uuid无效。");
        FruitProjectExample example = new FruitProjectExample();
        example.createCriteria().andUuidEqualTo(uuid);
        projectMapper.deleteByExample(example);
        FruitProjectDao projectDao = FruitProject.getProjectDao();
        projectDao.setUuid(uuid);
        Relation.getInstance(teamDao, userDao, projectDao)
                .removesUserRelation().removesTeamRelation();
    }

    @Override
    public List<UserProjectRelation> findJoin(UserProjectRelation relation) {
        return userDao.finds(relation);
    }

    @Override
    public List<ProjectTeamRelation> findJoin(ProjectTeamRelation relation) {
        return teamDao.finds(relation);
    }

    /**
     * 非静态类，对外部类提供关联功能
     */
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
            UserDao.remove(UserProjectRelation.newInstance(Dao.getUuid(), null));
            return this;
        }

        /**
         * 删除指定关联用户
         */
        private Relation removeUserRelation() {
            Dao.getUserRelation(FruitDict.Systems.DELETE).forEach((i) -> {
                if (StringUtils.isBlank(i.getUserId()))
                    throw new CheckException("未指明删除的关联用户");
                UserDao.remove(UserProjectRelation.newInstance(Dao.getUuid(), i.getUserId(), StringUtils.isNotBlank(i.getUpRole()) ? i.getUpRole() : null));
            });
            return this;
        }


        /**
         * 删除所有关联团队
         */
        private Relation removesTeamRelation() {
            TeamDao.remove(ProjectTeamRelation.newInstance(Dao.getUuid(), null));
            return this;
        }

        /**
         * 删除指定关联团队
         */
        private Relation removeTeamRelation() {
            Dao.getTeamRelation(FruitDict.Systems.DELETE).forEach((i) -> {
                if (StringUtils.isBlank(i.getTeamId()))
                    throw new CheckException("未指明删除的关联团队");
                TeamDao.remove(ProjectTeamRelation.newInstance(Dao.getUuid(), i.getTeamId(), StringUtils.isNotBlank(i.getTpRole()) ? i.getTpRole() : null));
            });
            return this;
        }


        /**
         * 添加用户关联
         */
        private Relation insertUserRelation() {
            Dao.getUserRelation(FruitDict.Systems.ADD).forEach((i) -> {
                i.setProjectId(Dao.getUuid());
                UserDao.insert(i);
            });
            return this;
        }

        /**
         * 添加团队关联
         */
        private Relation insertTeamRelation() {
            Dao.getTeamRelation(FruitDict.Systems.ADD).forEach((i) -> {
                i.setProjectId(Dao.getUuid());
                TeamDao.insert(i);
            });
            return this;
        }
    }
}
