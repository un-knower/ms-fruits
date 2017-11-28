package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.AbstractDaoRelation;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.example.ProjectTeamRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.ProjectTeamRelationMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by wangziwen on 2017/9/12.
 */
@Service
@Transactional
public class ProjectTeamDaoImpl<T extends ProjectTeamRelation> extends AbstractDaoRelation<T> {
    @Autowired
    private ProjectTeamRelationMapper mapper;

    @Override
    public void insert(ProjectTeamRelation relation) {
        if (StringUtils.isBlank(relation.getProjectId()) || StringUtils.isBlank(relation.getTeamId()))
            throw new CheckException(MessageFormat.format(checkMsg, "项目-团队"));
        mapper.insertSelective(relation);
    }

    @Override
    public void remove(ProjectTeamRelation relation) {
        mapper.deleteByExample(removeTemplate(relation));
    }

    private ProjectTeamRelationExample removeTemplate(ProjectTeamRelation relation) {
        final ProjectTeamRelationExample example = new ProjectTeamRelationExample();
        final ProjectTeamRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getProjectId()))
            criteria.andProjectIdEqualTo(relation.getProjectId());
        if (StringUtils.isNotBlank(relation.getTeamId()))
            criteria.andTeamIdEqualTo(relation.getTeamId());
        if (StringUtils.isNotBlank(relation.getTpRole()))
            criteria.andTpRoleEqualTo(relation.getTpRole());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckRelationException("【ProjectTeamDaoImpl.remove】缺少删除条件");
        return example;
    }

    public List<ProjectTeamRelation> finds(ProjectTeamRelation relation) {
        ProjectTeamRelationExample example = new ProjectTeamRelationExample();
        ProjectTeamRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getProjectId()))
            criteria.andProjectIdEqualTo(relation.getProjectId());
        if (StringUtils.isNotBlank(relation.getTeamId()))
            criteria.andTeamIdEqualTo(relation.getTeamId());
        if (StringUtils.isNotBlank(relation.getTpRole()))
            criteria.andTpRoleEqualTo(relation.getTpRole());
        return mapper.selectByExample(example);
    }

    @Override
    public void deleted(ProjectTeamRelation relation) {
        ProjectTeamRelation delete = ProjectTeamRelation.getInstance();
        delete.setIsDeleted(FruitDict.Systems.Y.name());
        mapper.updateByExampleSelective(delete, removeTemplate(relation));
    }
}
