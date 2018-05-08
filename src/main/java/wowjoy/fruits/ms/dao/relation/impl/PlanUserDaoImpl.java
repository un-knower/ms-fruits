package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.RelationInterface;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.entity.PlanUserRelation;
import wowjoy.fruits.ms.module.relation.example.PlanUserRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.PlanUserRelationMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

/**
 * Created by wangziwen on 2017/9/20.
 */
@Service
@Transactional
public class PlanUserDaoImpl<T extends PlanUserRelation, E extends PlanUserRelationExample> implements RelationInterface<T, E> {
    @Autowired
    private PlanUserRelationMapper mapper;

    @Override
    public void insert(PlanUserRelation relation) {
        if (StringUtils.isBlank(relation.getPlanId()) || StringUtils.isBlank(relation.getUserId()) || StringUtils.isBlank(relation.getPuRole()))
            throw new CheckException(MessageFormat.format(checkMsg, "计划-用户"));
        mapper.insertSelective(relation);
    }

    @Override
    public void insert(Consumer<T> tConsumer) {
        PlanUserRelation instance = PlanUserRelation.getInstance();
        tConsumer.accept((T) instance);
        instance.setPuRole(FruitDict.PlanUserDict.PRINCIPAL.name());
        Optional<PlanUserRelation> optionalInstance = Optional.of(instance);
        optionalInstance.map(PlanUserRelation::getPlanId).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException("缺少计划UUID"));
        optionalInstance.map(PlanUserRelation::getUserId).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException("缺少用户UUID"));
        mapper.insertSelective(instance);
    }

    @Override
    public void remove(PlanUserRelation relation) {
        mapper.deleteByExample(removeTemplate(relation));
    }

    private PlanUserRelationExample removeTemplate(PlanUserRelation relation) {
        final PlanUserRelationExample example = new PlanUserRelationExample();
        final PlanUserRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getPlanId()))
            criteria.andPlanIdEqualTo(relation.getPlanId());
        if (StringUtils.isNotBlank(relation.getUserId()))
            criteria.andUserIdEqualTo(relation.getUserId());
        if (StringUtils.isNotBlank(relation.getPuRole()))
            criteria.andPuRoleEqualTo(relation.getPuRole());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckRelationException("【PlanUserDaoImpl.remove】缺少删除条件");
        return example;
    }

    @Override
    public void deleted(T relation) {
        PlanUserRelation delete = new PlanUserRelation.Update();
        delete.setIsDeleted(Systems.Y.name());
        mapper.updateByExampleSelective(delete, removeTemplate(relation));
    }

    @Override
    public void deleted(Consumer<E> tConsumer) {
        PlanUserRelationExample example = new PlanUserRelationExample();
        tConsumer.accept((E) example);
        Optional.of(example.getOredCriteria())
                /*检查列表元素是否为空*/
                .map(criteriaList -> criteriaList.stream().filter(PlanUserRelationExample.Criteria::isValid).collect(toList()))
                .filter(criteriaList -> !criteriaList.isEmpty())
                .orElseThrow(() -> new CheckException("必须携带条件"));
        PlanUserRelation deleted = PlanUserRelation.getInstance();
        deleted.setIsDeleted(Systems.Y.name());
        mapper.updateByExampleSelective(deleted, example);
    }

    public List<PlanUserRelation> findByExample(Consumer<PlanUserRelationExample> exampleConsumer) {
        PlanUserRelationExample example = new PlanUserRelationExample();
        exampleConsumer.accept(example);
        return mapper.selectByExample(example);
    }
}
