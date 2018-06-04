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
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

/**
 * Created by wangziwen on 2017/9/12.
 */
@Service
@Transactional
public class TaskPlanDaoImpl<T extends TaskPlanRelation, E extends TaskPlanRelationExample> implements RelationInterface<T, E> {
    @Autowired
    private TaskPlanRelationMapper mapper;

    @Override
    public void insert(TaskPlanRelation relation) {
        if (StringUtils.isBlank(relation.getPlanId()) || StringUtils.isBlank(relation.getTaskId()))
            throw new CheckException(MessageFormat.format(checkMsg, "任务-计划"));
        mapper.insertSelective(relation);
    }

    @Override
    public void insert(Consumer<T> tConsumer) {
        TaskPlanRelation taskPlanRelation = new TaskPlanRelation();
        tConsumer.accept((T) taskPlanRelation);
        Optional<TaskPlanRelation> optional = Optional.of(taskPlanRelation);
        optional.map(TaskPlanRelation::getPlanId).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name()));
        optional.map(TaskPlanRelation::getTaskId).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name()));
        mapper.insertSelective(taskPlanRelation);
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
        TaskPlanRelation.Update delete = new TaskPlanRelation.Update();
        delete.setIsDeleted(Systems.Y.name());
        mapper.updateByExampleSelective(delete, removeTemplate(relation));
    }

    @Override
    public void deleted(Consumer<E> tConsumer) {
        TaskPlanRelationExample example = new TaskPlanRelationExample();
        tConsumer.accept((E) example);
        Optional.of(example.getOredCriteria())
                /*检查列表元素是否为空*/
                .map(criteriaList -> criteriaList.stream().filter(TaskPlanRelationExample.Criteria::isValid).collect(toList()))
                .filter(criteriaList -> !criteriaList.isEmpty())
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_LACK_CRITERIA.name()));
        TaskPlanRelation instance = new TaskPlanRelation.Update();
        instance.setIsDeleted(Systems.Y.name());
        mapper.updateByExampleSelective(instance, example);
    }
}
