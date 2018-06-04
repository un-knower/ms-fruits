package wowjoy.fruits.ms.dao.relation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.RelationInterface;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.entity.NotepadResourceRelation;
import wowjoy.fruits.ms.module.relation.example.NotepadResourceRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.NotepadResourceRelationMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.Optional;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
@Service
@Transactional
public class NotepadResourceDaoImpl<T extends NotepadResourceRelation, E extends NotepadResourceRelationExample> implements RelationInterface<T, E> {
    private final NotepadResourceRelationMapper relationMapper;

    @Autowired
    public NotepadResourceDaoImpl(NotepadResourceRelationMapper relationMapper) {
        this.relationMapper = relationMapper;
    }

    @Override
    public void insert(Consumer<T> consumer) {
        NotepadResourceRelation relation = new NotepadResourceRelation();
        consumer.accept((T) relation);
        relationMapper.insertSelective(relation);
    }

    @Override
    public void deleted(Consumer<E> tConsumer) {
        NotepadResourceRelationExample example = new NotepadResourceRelationExample();
        tConsumer.accept((E) example);
        Optional.of(example.getOredCriteria())
                /*检查列表元素是否为空*/
                .map(criteriaList -> criteriaList.stream().filter(NotepadResourceRelationExample.Criteria::isValid).collect(toList()))
                .filter(criteriaList -> !criteriaList.isEmpty())
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_LACK_CRITERIA.name()));
        NotepadResourceRelation.Update relation = new NotepadResourceRelation.Update();
        relation.setIsDeleted(FruitDict.Systems.Y.name());
        relationMapper.updateByExampleSelective(relation, example);
    }


}
