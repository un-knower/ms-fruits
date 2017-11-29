package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.AbstractDaoRelation;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.entity.TaskListRelation;
import wowjoy.fruits.ms.module.relation.example.TaskListRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.TaskListRelationMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by wangziwen on 2017/9/12.
 */
@Service
@Transactional
public class TaskListDaoImpl<T extends TaskListRelation> extends AbstractDaoRelation<T> {
    @Autowired
    private TaskListRelationMapper mapper;

    @Override
    public void insert(TaskListRelation relation) {
        if (StringUtils.isBlank(relation.getTaskId()) || StringUtils.isBlank(relation.getListId()))
            throw new CheckException(MessageFormat.format(checkMsg, "任务-列表"));
        mapper.insertSelective(relation);
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
        TaskListRelation delete = TaskListRelation.getInstance();
        delete.setIsDeleted(FruitDict.Systems.Y.name());
        mapper.updateByExampleSelective(delete, removeTemplate(relation));
    }
}
