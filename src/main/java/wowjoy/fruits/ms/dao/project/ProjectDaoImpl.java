package wowjoy.fruits.ms.dao.project;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.controller.vo.FruitProjectVo;
import wowjoy.fruits.ms.dao.relation.AbstractDaoRelation;
import wowjoy.fruits.ms.module.project.FruitProject;
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
    protected void insert() {
        projectMapper.insertSelective(this.getFruitProject());
        this.insertTeamRelation();
        this.insertUserRelation();
    }

    @Override
    protected FruitProjectVo getFruitProject() {
        return (FruitProjectVo) super.getFruitProject();
    }

    @Override
    protected List<FruitProject> finds() {
        FruitProjectExample example = new FruitProjectExample();
        final FruitProjectExample.Criteria criteria = example.createCriteria();
        if (this.getFruitProject().isNotEmpty()) {
            if (StringUtils.isNotBlank(this.getFruitProject().getTitle()))
                criteria.andTitleEqualTo(this.getFruitProject().getTitle());
            if (StringUtils.isNotBlank(this.getFruitProject().getProjectStatus()))
                criteria.andProjectStatusEqualTo(this.getFruitProject().getProjectStatus());
            if (StringUtils.isNotBlank(this.getFruitProject().getUuidVo()))
                criteria.andUuidEqualTo(this.getFruitProject().getUuidVo());
        }
        return projectMapper.selectUserRelationByExample(example);
    }


    @Override
    protected void update() {
        /*修改项目信息*/
        FruitProjectExample example = new FruitProjectExample();
        example.createCriteria().andUuidEqualTo(this.getFruitProject().getUuid());
        projectMapper.updateByExampleSelective(this.getFruitProject(), example);
        /*删除关联*/
        removeUserRelation().removeTeamRelation();
        /*添加关联*/
        insertUserRelation().insertTeamRelation();
    }

    @Override
    protected void updateStatus() {
        if (StringUtils.isBlank(this.getFruitProject().getUuid()))
            throw new CheckProjectException("【uuid】无效。");
        final FruitProjectExample example = new FruitProjectExample();
        example.createCriteria().andUuidEqualTo(this.getFruitProject().getUuid());
        final FruitProject data = FruitProject.getInstance();
        data.setUuid(this.getFruitProject().getUuid());
        data.setProjectStatus(this.getFruitProject().getProjectStatus());
        projectMapper.updateByExampleSelective(data, example);
    }

    /**
     * 删除所有关联用户
     */
    private ProjectDaoImpl removeUserRelation() {
        userDao.remove(UserProjectRelation.newInstance(this.getFruitProject().getUuid(), null));
        return this;
    }

    /**
     * 删除所有关联团队
     */
    private ProjectDaoImpl removeTeamRelation() {
        teamDao.remove(ProjectTeamRelation.newInstance(this.getFruitProject().getUuid(), null));
        return this;
    }

    /**
     * 添加用户关联
     */
    private ProjectDaoImpl insertUserRelation() {
        final FruitProjectVo data = (FruitProjectVo) this.getFruitProject();
        if (data.isNullUserVo()) return null;
        data.getUserVo().forEach((i) -> {
            i.setProjectId(this.getFruitProject().getUuid());
            userDao.insert(i);
        });
        return this;
    }

    /**
     * 添加团队关联
     */
    private ProjectDaoImpl insertTeamRelation() {
        final FruitProjectVo data = (FruitProjectVo) this.getFruitProject();
        if (data.isNullTeamVo()) return null;
        data.getTeamVo().forEach((i) -> {
            i.setProjectId(this.getFruitProject().getUuid());
            teamDao.insert(i);
        });
        return this;
    }
}
