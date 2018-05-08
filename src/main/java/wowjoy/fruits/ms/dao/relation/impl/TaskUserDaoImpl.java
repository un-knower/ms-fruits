package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.RelationInterface;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.entity.TaskUserRelation;
import wowjoy.fruits.ms.module.relation.example.TaskUserRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.TaskUserRelationMapper;
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
public class TaskUserDaoImpl<T extends TaskUserRelation, E extends TaskUserRelationExample> implements RelationInterface<T, E> {
    @Autowired
    private TaskUserRelationMapper mapper;

    @Override
    public void insert(TaskUserRelation relation) {
        if (StringUtils.isBlank(relation.getUserId()) || StringUtils.isBlank(relation.getTaskId()) || StringUtils.isBlank(relation.getUserRole()))
            throw new CheckException(MessageFormat.format(checkMsg, "任务-用户"));
        mapper.insertSelective(relation);
    }

    @Override
    public void insert(Consumer<T> tConsumer) {
        TaskUserRelation taskUserRelation = new TaskUserRelation();
        tConsumer.accept((T) taskUserRelation);
        taskUserRelation.setUserRole(FruitDict.TaskUserDict.EXECUTOR);
        Optional<TaskUserRelation> optional = Optional.of(taskUserRelation);
        optional.map(TaskUserRelation::getUserId).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException("task -> user userId can't null"));
        optional.map(TaskUserRelation::getTaskId).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException("task -> user taskId can't null"));
        mapper.insertSelective(taskUserRelation);
    }

    @Override
    public void remove(TaskUserRelation relation) {
        mapper.deleteByExample(removeTemplate(relation));
    }

    private TaskUserRelationExample removeTemplate(TaskUserRelation relation) {
        final TaskUserRelationExample example = new TaskUserRelationExample();
        final TaskUserRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getTaskId()))
            criteria.andTaskIdEqualTo(relation.getTaskId());
        if (StringUtils.isNotBlank(relation.getUserId()))
            criteria.andUserIdEqualTo(relation.getUserId());
        /*根据角色区分用户，移除必须携带角色*/
        if (StringUtils.isNotBlank(relation.getUserRole()))
            criteria.andUserRoleEqualTo(relation.getUserRole());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckRelationException("【TaskUserDaoImpl.remove】缺少删除条件");
        return example;
    }

    public List<TaskUserRelation> finds(TaskUserRelation relation) {
        TaskUserRelationExample example = new TaskUserRelationExample();
        TaskUserRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getUserId()))
            criteria.andUserIdEqualTo(relation.getUserId());
        if (StringUtils.isNotBlank(relation.getTaskId()))
            criteria.andTaskIdEqualTo(relation.getTaskId());
        if (StringUtils.isNotBlank(relation.getUserRole()))
            criteria.andUserRoleEqualTo(relation.getUserRole());
        return mapper.selectByExample(example);
    }

    @Override
    public void deleted(TaskUserRelation relation) {
        TaskUserRelation delete = new TaskUserRelation.Update();
        delete.setIsDeleted(Systems.Y.name());
        mapper.updateByExampleSelective(delete, removeTemplate(relation));
    }

    @Override
    public void deleted(Consumer<E> tConsumer) {
        TaskUserRelationExample example = new TaskUserRelationExample();
        tConsumer.accept((E) example);
        Optional.of(example.getOredCriteria())
                /*检查列表元素是否为空*/
                .map(criteriaList -> criteriaList.stream().filter(TaskUserRelationExample.Criteria::isValid).collect(toList()))
                .filter(criteriaList -> !criteriaList.isEmpty())
                .orElseThrow(() -> new CheckException("必须携带条件"));
        TaskUserRelation instance = new TaskUserRelation.Update();
        instance.setIsDeleted(Systems.Y.name());
        mapper.updateByExampleSelective(instance, example);
    }

    public List<TaskUserRelation> findByExample(Consumer<TaskUserRelationExample> exampleConsumer) {
        TaskUserRelationExample example = new TaskUserRelationExample();
        exampleConsumer.accept(example);
        return mapper.selectByExample(example);
    }
}
