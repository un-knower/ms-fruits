package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.RelationInterface;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.relation.example.UserProjectRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.UserProjectRelationMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by wangziwen on 2017/9/12.
 */
@Service
@Transactional
public class UserProjectDaoImpl<T extends UserProjectRelation,E extends UserProjectRelationExample> implements RelationInterface<T,E> {
    @Autowired
    private UserProjectRelationMapper mapper;

    @Override
    public void insert(UserProjectRelation relation) {
        if (StringUtils.isBlank(relation.getProjectId()) || StringUtils.isBlank(relation.getUserId()) || StringUtils.isBlank(relation.getUpRole()))
            throw new CheckException(MessageFormat.format(checkMsg, "用户-项目"));
        mapper.insertSelective(relation);
    }

    @Override
    public void remove(UserProjectRelation relation) {
        mapper.deleteByExample(removeTemplate(relation));
    }

    private UserProjectRelationExample removeTemplate(UserProjectRelation relation) {
        final UserProjectRelationExample example = new UserProjectRelationExample();
        final UserProjectRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getProjectId()))
            criteria.andProjectIdEqualTo(relation.getProjectId());
        if (StringUtils.isNotBlank(relation.getUserId()))
            criteria.andUserIdEqualTo(relation.getUserId());
        /*根据角色区分用户，移除必须携带角色*/
        if (StringUtils.isNotBlank(relation.getUpRole()))
            criteria.andUpRoleEqualTo(relation.getUpRole());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckRelationException("【UserProjectDaoImpl.remove】缺少关联条件");
        return example;
    }

    public List<UserProjectRelation> finds(UserProjectRelation relation) {
        UserProjectRelationExample example = new UserProjectRelationExample();
        UserProjectRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getProjectId()))
            criteria.andProjectIdEqualTo(relation.getProjectId());
        if (StringUtils.isNotBlank(relation.getUserId()))
            criteria.andUserIdEqualTo(relation.getUserId());
        if (StringUtils.isNotBlank(relation.getUpRole()))
            criteria.andUpRoleEqualTo(relation.getUpRole());
        return mapper.selectByExample(example);
    }

    @Override
    public void deleted(T relation) {
        UserProjectRelation delete = new UserProjectRelation.Update();
        delete.setIsDeleted(FruitDict.Systems.Y.name());
        mapper.updateByExampleSelective(delete, removeTemplate(relation));
    }
}
