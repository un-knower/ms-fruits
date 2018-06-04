package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.RelationInterface;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.entity.TaskListRelation;
import wowjoy.fruits.ms.module.relation.example.TaskListRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.TaskListRelationMapper;
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
public class TaskListDaoImpl<T extends TaskListRelation, E extends TaskListRelationExample> implements RelationInterface<T, E> {
    @Autowired
    private TaskListRelationMapper mapper;

    @Override
    public void insert(TaskListRelation relation) {
        if (StringUtils.isBlank(relation.getTaskId()) || StringUtils.isBlank(relation.getListId()))
            throw new CheckException(MessageFormat.format(checkMsg, "任务-列表"));
        mapper.insertSelective(relation);
    }

    @Override
    public void insert(Consumer<T> tConsumer) {
        TaskListRelation taskListRelation = new TaskListRelation();
        tConsumer.accept((T) taskListRelation);
        Optional<TaskListRelation> optional = Optional.of(taskListRelation);
        optional.map(TaskListRelation::getTaskId).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name()));
        optional.map(TaskListRelation::getListId).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name()));
        mapper.insertSelective(taskListRelation);
    }

    @Override
    public void remove(TaskListRelation relation) {
        mapper.deleteByExample(removeTemplate(relation));
    }

    private TaskListRelationExample removeTemplate(TaskListRelation relation) {
        final TaskListRelationExample example = new TaskListRelationExample();
        final TaskListRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getTaskId()))
            criteria.andTaskIdEqualTo(relation.getTaskId());
        if (StringUtils.isNotBlank(relation.getListId()))
            criteria.andListIdEqualTo(relation.getListId());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckRelationException("【TaskListDaoImpl.remove】缺少删除条件");
        return example;
    }

    public List<TaskListRelation> finds(TaskListRelation relation) {
        TaskListRelationExample example = new TaskListRelationExample();
        TaskListRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getListId()))
            criteria.andListIdEqualTo(relation.getListId());
        if (StringUtils.isNotBlank(relation.getTaskId()))
            criteria.andTaskIdEqualTo(relation.getTaskId());
        return mapper.selectByExample(example);
    }

    @Override
    public void deleted(TaskListRelation relation) {
        TaskListRelation.Update delete = new TaskListRelation.Update();
        delete.setIsDeleted(Systems.Y.name());
        mapper.updateByExampleSelective(delete, removeTemplate(relation));
    }

    @Override
    public void deleted(Consumer<E> tConsumer) {
        TaskListRelationExample example = new TaskListRelationExample();
        tConsumer.accept((E) example);
        Optional.of(example.getOredCriteria())
                /*检查列表元素是否为空*/
                .map(criteriaList -> criteriaList.stream().filter(TaskListRelationExample.Criteria::isValid).collect(toList()))
                .filter(criteriaList -> !criteriaList.isEmpty())
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_LACK_CRITERIA.name()));
        TaskListRelation.Update taskListRelation = new TaskListRelation.Update();
        taskListRelation.setIsDeleted(Systems.Y.name());
        mapper.updateByExampleSelective(taskListRelation, example);

    }
}
