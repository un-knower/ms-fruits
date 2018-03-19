package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.RelationInterface;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.entity.PlanProjectRelation;
import wowjoy.fruits.ms.module.relation.example.PlanProjectRelationExample;
import wowjoy.fruits.ms.module.relation.example.PlanUserRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.PlanProjectRelationMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

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
public class PlanProjectDaoImpl<T extends PlanProjectRelation, E extends PlanProjectRelationExample> implements RelationInterface<T, E> {
    @Autowired
    private PlanProjectRelationMapper mapper;

    @Override
    public void insert(PlanProjectRelation relation) {
        if (StringUtils.isBlank(relation.getPlanId()) || StringUtils.isBlank(relation.getProjectId()))
            throw new CheckException(MessageFormat.format(checkMsg, "计划-项目"));
        mapper.insertSelective(relation);
    }

    @Override
    public void insert(Consumer<T> tConsumer) {
        PlanProjectRelation instance = PlanProjectRelation.getInstance();
        tConsumer.accept((T) instance);
        Optional<PlanProjectRelation> optionalInstance = Optional.of(instance);
        optionalInstance.map(PlanProjectRelation::getPlanId).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException("缺少计划UUID"));
        optionalInstance.map(PlanProjectRelation::getProjectId).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException("缺少项目UUID"));
        mapper.insertSelective(instance);
    }

    @Override
    public void remove(PlanProjectRelation relation) {
        mapper.deleteByExample(removeTemplate(relation));
    }

    private PlanProjectRelationExample removeTemplate(PlanProjectRelation relation) {
        final PlanProjectRelationExample example = new PlanProjectRelationExample();
        final PlanProjectRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getPlanId()))
            criteria.andPlanIdEqualTo(relation.getPlanId());
        if (StringUtils.isNotBlank(relation.getProjectId()))
            criteria.andProjectIdEqualTo(relation.getProjectId());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckRelationException("【PlanProjectDaoImpl.remove】缺少删除条件");
        return example;
    }

    public List<PlanProjectRelation> finds(PlanProjectRelation relation) {
        PlanProjectRelationExample example = new PlanProjectRelationExample();
        PlanProjectRelationExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(relation.getProjectId()))
            criteria.andProjectIdEqualTo(relation.getProjectId());
        if (StringUtils.isNotBlank(relation.getPlanId()))
            criteria.andPlanIdEqualTo(relation.getPlanId());
        return mapper.selectByExample(example);
    }

    @Override
    public void deleted(T relation) {
        PlanProjectRelation delete = PlanProjectRelation.getInstance();
        delete.setIsDeleted(FruitDict.Systems.Y.name());
        mapper.updateByExampleSelective(delete, removeTemplate(relation));
    }

    @Override
    public void deleted(Consumer<E> tConsumer) {
        PlanProjectRelationExample example = new PlanProjectRelationExample();
        tConsumer.accept((E) example);
        Optional.of(example.getOredCriteria())
                /*检查列表元素是否为空*/
                .map(criteriaList -> criteriaList.stream().filter(PlanProjectRelationExample.Criteria::isValid).collect(toList()))
                .filter(criteriaList -> !criteriaList.isEmpty())
                .orElseThrow(() -> new CheckException("必须携带条件"));
        PlanProjectRelation delete = PlanProjectRelation.getInstance();
        delete.setIsDeleted(FruitDict.Systems.Y.name());
        mapper.updateByExampleSelective(delete,example);
    }
}
