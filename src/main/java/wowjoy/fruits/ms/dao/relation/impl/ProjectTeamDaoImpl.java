package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.AbstractDaoRelation;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.example.ProjectTeamRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.ProjectTeamRelationMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

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
        relation.checkTpRole();
        mapper.insertSelective(relation);
    }

    @Override
    public void remove(ProjectTeamRelation relation) {
        final ProjectTeamRelationExample example = new ProjectTeamRelationExample();
        final ProjectTeamRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getProjectId()))
            criteria.andProjectIdEqualTo(relation.getProjectId());
        else if (StringUtils.isNotBlank(relation.getTeamId()))
            criteria.andTeamIdEqualTo(relation.getTeamId());
        else
            throw new CheckRelationException("【ProjectTeamDaoImpl.remove】缺少删除条件");
        mapper.deleteByExample(example);
    }
}
