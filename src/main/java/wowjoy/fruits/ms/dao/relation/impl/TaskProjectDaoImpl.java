package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.RelationInterface;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.entity.TaskProjectRelation;
import wowjoy.fruits.ms.module.relation.example.TaskListRelationExample;
import wowjoy.fruits.ms.module.relation.example.TaskProjectRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.TaskProjectRelationMapper;
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
public class TaskProjectDaoImpl<T extends TaskProjectRelation, E extends TaskProjectRelationExample> implements RelationInterface<T, E> {
    @Autowired
    private TaskProjectRelationMapper mapper;

    @Override
    public void insert(TaskProjectRelation relation) {
        if (StringUtils.isBlank(relation.getTaskId()) || StringUtils.isBlank(relation.getProjectId()))
            throw new CheckException(MessageFormat.format(checkMsg, "任务-项目"));
        mapper.insertSelective(relation);
    }

    @Override
    public void insert(Consumer<T> tConsumer) {
        TaskProjectRelation taskProjectRelation = new TaskProjectRelation();
        tConsumer.accept((T) taskProjectRelation);
        Optional<TaskProjectRelation> optional = Optional.of(taskProjectRelation);
        optional.map(TaskProjectRelation::getTaskId).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException("task -> project taskId can't null"));
        optional.map(TaskProjectRelation::getProjectId).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException("task -> project projectId can't null"));
        mapper.insertSelective(taskProjectRelation);
    }

    @Override
    public void remove(TaskProjectRelation relation) {
        mapper.deleteByExample(removeTemplate(relation));
    }

    private TaskProjectRelationExample removeTemplate(TaskProjectRelation relation) {
        final TaskProjectRelationExample example = new TaskProjectRelationExample();
        final TaskProjectRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getTaskId()))
            criteria.andTaskIdEqualTo(relation.getTaskId());
        if (StringUtils.isNotBlank(relation.getProjectId()))
            criteria.andProjectIdEqualTo(relation.getProjectId());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckRelationException("【TaskProjectDaoImpl.remove】缺少删除条件");
        return example;
    }

    public List<TaskProjectRelation> finds(TaskProjectRelation relation) {
        TaskProjectRelationExample example = new TaskProjectRelationExample();
        TaskProjectRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getProjectId()))
            criteria.andProjectIdEqualTo(relation.getProjectId());
        if (StringUtils.isNotBlank(relation.getTaskId()))
            criteria.andTaskIdEqualTo(relation.getTaskId());
        return mapper.selectByExample(example);
    }

    @Override
    public void deleted(TaskProjectRelation relation) {
        TaskProjectRelation delete = TaskProjectRelation.getInstance();
        delete.setIsDeleted(Systems.Y.name());
        mapper.updateByExampleSelective(delete, removeTemplate(relation));
    }

    @Override
    public void deleted(Consumer<E> tConsumer) {
        TaskProjectRelationExample example = new TaskProjectRelationExample();
        tConsumer.accept((E) example);
        Optional.of(example.getOredCriteria())
                /*检查列表元素是否为空*/
                .map(criteriaList -> criteriaList.stream().filter(TaskProjectRelationExample.Criteria::isValid).collect(toList()))
                .filter(criteriaList -> !criteriaList.isEmpty())
                .orElseThrow(() -> new CheckException("必须携带条件"));
        TaskProjectRelation instance = TaskProjectRelation.getInstance();
        instance.setIsDeleted(Systems.Y.name());
        mapper.updateByExampleSelective(instance, example);
    }
}
