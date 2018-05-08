package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.RelationInterface;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.entity.ProjectListRelation;
import wowjoy.fruits.ms.module.relation.example.ProjectListRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.ProjectListRelationMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by wangziwen on 2017/9/12.
 */
@Service
@Transactional
public class ProjectListDaoImpl<T extends ProjectListRelation, E extends ProjectListRelationExample> implements RelationInterface<T, E> {
    @Autowired
    private ProjectListRelationMapper mapper;

    @Override
    public void insert(ProjectListRelation relation) {
        if (StringUtils.isBlank(relation.getProjectId()) || StringUtils.isBlank(relation.getListId()))
            throw new CheckException(MessageFormat.format(checkMsg, "项目-列表"));
        mapper.insertSelective(relation);
    }

    @Override
    public void remove(ProjectListRelation relation) {
        mapper.deleteByExample(removeTemplate(relation));
    }

    public List<ProjectListRelation> finds(ProjectListRelation relation) {
        ProjectListRelationExample example = new ProjectListRelationExample();
        ProjectListRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getProjectId()))
            criteria.andProjectIdEqualTo(relation.getProjectId());
        if (StringUtils.isNotBlank(relation.getListId()))
            criteria.andListIdEqualTo(relation.getListId());
        return mapper.selectByExample(example);
    }

    @Override
    public void deleted(T relation) {
        ProjectListRelation.Update deleted = new ProjectListRelation.Update();
        deleted.setIsDeleted(FruitDict.Systems.Y.name());
        mapper.updateByExampleSelective(deleted, removeTemplate(relation));
    }

    private ProjectListRelationExample removeTemplate(ProjectListRelation relation) {
        final ProjectListRelationExample example = new ProjectListRelationExample();
        final ProjectListRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getProjectId()))
            criteria.andProjectIdEqualTo(relation.getProjectId());
        if (StringUtils.isNotBlank(relation.getListId()))
            criteria.andListIdEqualTo(relation.getListId());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckRelationException("【ProjectListDaoImpl.remove】缺少删除条件");
        return example;
    }
}
