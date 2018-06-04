package wowjoy.fruits.ms.dao.relation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.RelationInterface;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.entity.DefectResourceRelation;
import wowjoy.fruits.ms.module.relation.example.DefectResourceRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.DefectResourceRelationMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.Optional;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
@Service
@Transactional
public class DefectResourceDaoImpl<T extends DefectResourceRelation, E extends DefectResourceRelationExample> implements RelationInterface<T, E> {
    private final DefectResourceRelationMapper relationMapper;

    @Autowired
    public DefectResourceDaoImpl(DefectResourceRelationMapper relationMapper) {
        this.relationMapper = relationMapper;
    }

    @Override
    public void insert(Consumer<T> consumer) {
        DefectResourceRelation relation = new DefectResourceRelation();
        consumer.accept((T) relation);
        relationMapper.insertSelective(relation);
    }

    @Override
    public void deleted(Consumer<E> tConsumer) {
        DefectResourceRelationExample example = new DefectResourceRelationExample();
        tConsumer.accept((E) example);
        Optional.of(example.getOredCriteria())
                /*检查列表元素是否为空*/
                .map(criteriaList -> criteriaList.stream().filter(DefectResourceRelationExample.Criteria::isValid).collect(toList()))
                .filter(criteriaList -> !criteriaList.isEmpty())
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_LACK_CRITERIA.name()));
        DefectResourceRelation.Update relation = new DefectResourceRelation.Update();
        relation.setIsDeleted(FruitDict.Systems.Y.name());
        relationMapper.updateByExampleSelective(relation, example);
    }


}
