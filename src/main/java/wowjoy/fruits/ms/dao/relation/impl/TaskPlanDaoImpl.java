package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.AbstractDaoRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskPlanRelation;
import wowjoy.fruits.ms.module.relation.example.TaskPlanRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.TaskPlanRelationMapper;

/**
 * Created by wangziwen on 2017/9/12.
 */
@Service
@Transactional
public class TaskPlanDaoImpl<T extends TaskPlanRelation> extends AbstractDaoRelation<T> {
    @Autowired
    private TaskPlanRelationMapper mapper;

    @Override
    public void insert(TaskPlanRelation relation) {
        mapper.insertSelective(relation);
    }

    @Override
    public void remove(TaskPlanRelation relation) {
        final TaskPlanRelationExample example = new TaskPlanRelationExample();
        final TaskPlanRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getTaskId()))
            criteria.andTaskIdEqualTo(relation.getTaskId());
        if (StringUtils.isNotBlank(relation.getPlanId()))
            criteria.andPlanIdEqualTo(relation.getPlanId());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckRelationException("【TaskPlanDaoImpl.remove】缺少删除条件");
        mapper.deleteByExample(example);
    }

}
