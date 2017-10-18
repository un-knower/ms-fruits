package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.AbstractDaoRelation;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.entity.TaskUserRelation;
import wowjoy.fruits.ms.module.relation.example.TaskUserRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.TaskUserRelationMapper;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by wangziwen on 2017/9/12.
 */
@Service
@Transactional
public class TaskUserDaoImpl<T extends TaskUserRelation> extends AbstractDaoRelation<T> {
    @Autowired
    private TaskUserRelationMapper mapper;

    @Override
    public void insert(TaskUserRelation relation) {
        if (StringUtils.isBlank(relation.getUserId()) || StringUtils.isBlank(relation.getTaskId()) || StringUtils.isBlank(relation.getUserRole()))
            throw new CheckException(MessageFormat.format(checkMsg, "任务-用户"));
        mapper.insertSelective(relation);
    }

    @Override
    public void remove(TaskUserRelation relation) {
        final TaskUserRelationExample example = new TaskUserRelationExample();
        final TaskUserRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getTaskId()))
            criteria.andTaskIdEqualTo(relation.getTaskId());
        if (StringUtils.isNotBlank(relation.getUserId()))
            criteria.andUserIdEqualTo(relation.getUserId());
        if (StringUtils.isNotBlank(relation.getUserRole()))
            criteria.andUserRoleEqualTo(relation.getUserRole());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckRelationException("【TaskUserDaoImpl.remove】缺少删除条件");
        mapper.deleteByExample(example);
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

}
