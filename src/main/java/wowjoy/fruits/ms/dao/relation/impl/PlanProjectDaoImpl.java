package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.AbstractDaoRelation;
import wowjoy.fruits.ms.module.relation.entity.PlanProjectRelation;
import wowjoy.fruits.ms.module.relation.example.PlanProjectRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.PlanProjectRelationMapper;

/**
 * Created by wangziwen on 2017/9/20.
 */
@Service
@Transactional
public class PlanProjectDaoImpl<T extends PlanProjectRelation> extends AbstractDaoRelation<T> {
    @Autowired
    private PlanProjectRelationMapper mapper;

    @Override
    public void insert(PlanProjectRelation relation) {
        mapper.insertSelective(relation);
    }

    @Override
    public void remove(PlanProjectRelation relation) {
        final PlanProjectRelationExample example = new PlanProjectRelationExample();
        final PlanProjectRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getPlanId()))
            criteria.andPlanIdEqualTo(relation.getPlanId());
        if (StringUtils.isNotBlank(relation.getProjectId()))
            criteria.andProjectIdEqualTo(relation.getProjectId());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckRelationException("【PlanProjectDaoImpl.remove】缺少删除条件");
        mapper.deleteByExample(example);
    }
}
