package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.AbstractDaoRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.relation.example.UserProjectRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.UserProjectRelationMapper;

/**
 * Created by wangziwen on 2017/9/12.
 */
@Service
@Transactional
public class UserProjectDaoImpl<T extends UserProjectRelation> extends AbstractDaoRelation<T> {
    @Autowired
    private UserProjectRelationMapper mapper;

    @Override
    public void insert(UserProjectRelation relation) {
        relation.checkUpRole();
        mapper.insertSelective(relation);
    }

    @Override
    public void remove(UserProjectRelation term) {
        final UserProjectRelationExample example = new UserProjectRelationExample();
        final UserProjectRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(term.getProjectId()))
            criteria.andProjectIdEqualTo(term.getProjectId());
        else if (StringUtils.isNotBlank(term.getUserId()))
            criteria.andUserIdEqualTo(term.getUserId());
        else
            throw new CheckRelationException("【UserProjectDaoImpl.remove】缺少关联条件");
        mapper.deleteByExample(example);
    }

}
