package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.AbstractDaoRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskProjectRelation;
import wowjoy.fruits.ms.module.relation.example.TaskProjectRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.TaskProjectRelationMapper;

/**
 * Created by wangziwen on 2017/9/12.
 */
@Service
@Transactional
public class TaskProjectDaoImpl<T extends TaskProjectRelation> extends AbstractDaoRelation<T> {
    @Autowired
    private TaskProjectRelationMapper mapper;

    @Override
    public void insert(TaskProjectRelation relation) {
        mapper.insertSelective(relation);
    }

    @Override
    public void remove(TaskProjectRelation relation) {
        final TaskProjectRelationExample example = new TaskProjectRelationExample();
        final TaskProjectRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getTaskId()))
            criteria.andTaskIdEqualTo(relation.getTaskId());
        if (StringUtils.isNotBlank(relation.getProjectId()))
            criteria.andProjectIdEqualTo(relation.getProjectId());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckRelationException("【TaskProjectDaoImpl.remove】缺少删除条件");
        mapper.deleteByExample(example);
    }

}
