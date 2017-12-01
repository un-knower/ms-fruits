package wowjoy.fruits.ms.dao.project;

import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.impl.ProjectTeamDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.UserProjectDaoImpl;
import wowjoy.fruits.ms.dao.user.UserDaoImpl;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.project.FruitProjectDao;
import wowjoy.fruits.ms.module.project.FruitProjectExample;
import wowjoy.fruits.ms.module.project.mapper.FruitProjectMapper;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.user.FruitUserDao;
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
    private ProjectTeamDaoImpl teamRelationDao;
    @Qualifier("userProjectDaoImpl")
    @Autowired
    private UserProjectDaoImpl userRelationDao;
    @Autowired
    private UserDaoImpl userDao;


    @Override
    public void insert(FruitProjectDao dao) {
        projectMapper.insertSelective(dao);
        Relation.getInstance(teamRelationDao, userRelationDao, dao).insertTeamRelation().insertUserRelation();
    }

    @Override
    public List<FruitProjectDao> finds(FruitProjectDao dao) {
        final FruitProjectExample example = new FruitProjectExample();
        final FruitProjectExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getTitle()))
            criteria.andTitleLike(MessageFormat.format("%{0}%", dao.getTitle()));
        if (StringUtils.isNotBlank(dao.getProjectStatus()))
            criteria.andProjectStatusEqualTo(dao.getProjectStatus());
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name());
        String order = dao.sortConstrue();
        if (StringUtils.isNotBlank(order))
            example.setOrderByClause(order);
        return projectMapper.selectByExample(example);
    }

    @Override
    public List<FruitProjectDao> findUserByProjectIds(String... ids) {
        if (Arrays.isNullOrEmpty(ids))
            throw new CheckException("查询关联用户时，必须提供项目id");
        return projectMapper.selectUserByProjectId(ids);
    }

    @Override
    public List<FruitProjectDao> findTeamByProjectIds(String... ids) {
        if (Arrays.isNullOrEmpty(ids))
            throw new CheckException("查询关联团队时，必须提供项目id");
        return projectMapper.selectUserByProjectId(ids);
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
        criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name());
        example.setOrderByClause("create_date_time desc");
        return projectMapper.selectUserRelationByExample(example);
    }

    @Override
    public FruitProjectDao findByUUID(String uuid) {
        if (StringUtils.isBlank(uuid))
            throw new CheckException("【项目】uuId不能为空");
        FruitProjectExample example = new FruitProjectExample();
        example.createCriteria().andUuidEqualTo(uuid).andIsDeletedEqualTo(FruitDict.Systems.N.name());
        List<FruitProjectDao> data = projectMapper.selectUserRelationByExample(example);
        if (data.isEmpty())
            throw new CheckException("不存在！再查自杀");
        return data.get(0);
    }


    @Override
    public void update(FruitProjectDao dao) {
        /*修改项目信息*/
        FruitProjectExample example = new FruitProjectExample();
        example.createCriteria().andUuidEqualTo(dao.getUuid());
        projectMapper.updateByExampleSelective(dao, example);
        /*删除关联*/
        Relation.getInstance(teamRelationDao, userRelationDao, dao).removeUserRelation().removeTeamRelation();
        /*添加关联*/
        Relation.getInstance(teamRelationDao, userRelationDao, dao).insertTeamRelation().insertUserRelation();
    }

    @Override
    public void delete(String uuid) {
        if (StringUtils.isBlank(uuid))
            throw new CheckException("【项目】uuid无效。");
        FruitProjectExample example = new FruitProjectExample();
        example.createCriteria().andUuidEqualTo(uuid);
        FruitProjectDao delete = FruitProject.getDao();
        delete.setIsDeleted(FruitDict.Systems.Y.name());
        projectMapper.updateByExampleSelective(delete, example);
        FruitProjectDao projectDao = FruitProject.getDao();
        projectDao.setUuid(uuid);
        Relation.getInstance(teamRelationDao, userRelationDao, projectDao)
                .removesUserRelation().removesTeamRelation();
    }

    @Override
    public List<UserProjectRelation> findJoin(UserProjectRelation relation) {
        return userRelationDao.finds(relation);
    }

    @Override
    public List<ProjectTeamRelation> findJoin(ProjectTeamRelation relation) {
        return teamRelationDao.finds(relation);
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
            UserDao.deleted(UserProjectRelation.newInstance(Dao.getUuid(), null));
            return this;
        }

        /**
         * 删除指定关联用户
         */
        private Relation removeUserRelation() {
            Dao.getUserRelation(FruitDict.Systems.DELETE).forEach((i) -> {
                if (StringUtils.isBlank(i.getUserId()))
                    throw new CheckException("未指明删除的关联用户");
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
            Dao.getTeamRelation(FruitDict.Systems.DELETE).forEach((i) -> {
                if (StringUtils.isBlank(i.getTeamId()))
                    throw new CheckException("未指明删除的关联团队");
                TeamDao.deleted(ProjectTeamRelation.newInstance(Dao.getUuid(), i.getTeamId(), StringUtils.isNotBlank(i.getTpRole()) ? i.getTpRole() : null));
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
