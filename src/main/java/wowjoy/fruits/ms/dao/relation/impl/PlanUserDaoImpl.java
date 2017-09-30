package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.AbstractDaoRelation;
import wowjoy.fruits.ms.module.relation.entity.PlanUserRelation;
import wowjoy.fruits.ms.module.relation.example.PlanUserRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.PlanUserRelationMapper;

/**
 * Created by wangziwen on 2017/9/20.
 */
@Service
@Transactional
public class PlanUserDaoImpl<T extends PlanUserRelation> extends AbstractDaoRelation<T> {
    @Autowired
    private PlanUserRelationMapper mapper;

    @Override
    public void insert(PlanUserRelation relation) {
        relation.checkPuRole();
        mapper.insertSelective(relation);
    }

    @Override
    public void remove(PlanUserRelation relation) {
        final PlanUserRelationExample example = new PlanUserRelationExample();
        final PlanUserRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getPlanId()))
            criteria.andPlanIdEqualTo(relation.getPlanId());
        if (StringUtils.isNotBlank(relation.getUserId()))
            criteria.andUserIdEqualTo(relation.getUserId());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckRelationException("【PlanUserDaoImpl.remove】缺少删除条件");
        mapper.deleteByExample(example);
    }
}
