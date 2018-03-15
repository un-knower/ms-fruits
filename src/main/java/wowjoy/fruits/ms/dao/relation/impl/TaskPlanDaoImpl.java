package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.RelationInterface;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.entity.TaskPlanRelation;
import wowjoy.fruits.ms.module.relation.example.TaskPlanRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.TaskPlanRelationMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by wangziwen on 2017/9/12.
 */
@Service
@Transactional
public class TaskPlanDaoImpl<T extends TaskPlanRelation,E extends TaskPlanRelationExample> implements RelationInterface<T,E> {
    @Autowired
    private TaskPlanRelationMapper mapper;

    @Override
    public void insert(TaskPlanRelation relation) {
        if (StringUtils.isBlank(relation.getPlanId()) || StringUtils.isBlank(relation.getTaskId()))
            throw new CheckException(MessageFormat.format(checkMsg, "任务-计划"));
        mapper.insertSelective(relation);
    }

    @Override
    public void remove(TaskPlanRelation relation) {
        mapper.deleteByExample(removeTemplate(relation));
    }

    private TaskPlanRelationExample removeTemplate(TaskPlanRelation relation) {
        final TaskPlanRelationExample example = new TaskPlanRelationExample();
        final TaskPlanRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getTaskId()))
            criteria.andTaskIdEqualTo(relation.getTaskId());
        if (StringUtils.isNotBlank(relation.getPlanId()))
            criteria.andPlanIdEqualTo(relation.getPlanId());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckRelationException("【TaskPlanDaoImpl.remove】缺少删除条件");
        return example;
    }

    public List<TaskPlanRelation> finds(TaskPlanRelation relation) {
        TaskPlanRelationExample example = new TaskPlanRelationExample();
        TaskPlanRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getTaskId()))
            criteria.andTaskIdEqualTo(relation.getTaskId());
        if (StringUtils.isNotBlank(relation.getPlanId()))
            criteria.andPlanIdEqualTo(relation.getPlanId());
        return mapper.selectByExample(example);
    }


    @Override
    public void deleted(TaskPlanRelation relation) {
        TaskPlanRelation delete = TaskPlanRelation.getInstance();
        delete.setIsDeleted(FruitDict.Systems.Y.name());
        mapper.updateByExampleSelective(delete, removeTemplate(relation));
    }
}
