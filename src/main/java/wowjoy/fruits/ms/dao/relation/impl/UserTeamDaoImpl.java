package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.AbstractDaoRelation;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.entity.UserTeamRelation;
import wowjoy.fruits.ms.module.relation.example.UserTeamRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.UserTeamRelationMapper;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by wangziwen on 2017/9/12.
 */
@Service
@Transactional
public class UserTeamDaoImpl<T extends UserTeamRelation> extends AbstractDaoRelation<T> {
    @Autowired
    private UserTeamRelationMapper mapper;

    @Override
    public void insert(UserTeamRelation relation) {
        if (StringUtils.isBlank(relation.getTeamId()) || StringUtils.isBlank(relation.getUserId()) || StringUtils.isBlank(relation.getUtRole()))
            throw new CheckException(MessageFormat.format(checkMsg, "用户-团队"));
        mapper.insertSelective(relation);
    }

    @Override
    public void remove(UserTeamRelation relation) {
        final UserTeamRelationExample example = new UserTeamRelationExample();
        final UserTeamRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getTeamId()))
            criteria.andTeamIdEqualTo(relation.getTeamId());
        if (StringUtils.isNotBlank(relation.getUserId()))
            criteria.andUserIdEqualTo(relation.getUserId());
        if (StringUtils.isNotBlank(relation.getUtRole()))
            criteria.andUtRoleEqualTo(relation.getUtRole());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckRelationException("【UserTeamDaoImpl.remove】缺少关联条件");
        mapper.deleteByExample(example);
    }

    public List<UserTeamRelation> finds(UserTeamRelation relation) {
        UserTeamRelationExample example = new UserTeamRelationExample();
        UserTeamRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getTeamId()))
            criteria.andTeamIdEqualTo(relation.getTeamId());
        if (StringUtils.isNotBlank(relation.getUserId()))
            criteria.andUserIdEqualTo(relation.getUserId());
        if (StringUtils.isNotBlank(relation.getUtRole()))
            criteria.andUtRoleEqualTo(relation.getUtRole());
        return mapper.selectByExample(example);
    }

}
