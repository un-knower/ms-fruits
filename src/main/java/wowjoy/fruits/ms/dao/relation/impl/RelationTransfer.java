package wowjoy.fruits.ms.dao.relation.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.RelationInterface;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.entity.TransferUserRelation;
import wowjoy.fruits.ms.module.relation.example.TransferUserRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.TransferUserRelationMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.function.Consumer;

/**
 * Created by wangziwen on 2018/3/9.
 */
@Service
@Transactional
public class RelationTransfer<T extends TransferUserRelation, E extends TransferUserRelationExample> implements RelationInterface<T, E> {
    @Autowired

    private TransferUserRelationMapper mapper;

    @Override
    public void insert(Consumer<T> tConsumer) {
        TransferUserRelation relation = new TransferUserRelation();
        tConsumer.accept((T) relation);
        if (StringUtils.isBlank(relation.getTransferId())
                || StringUtils.isBlank(relation.getUserId())
                || StringUtils.isBlank(relation.getStatus()))
            throw new CheckException("用户转交记录参数不完整，无法关联");
        mapper.insertSelective(relation);
    }

    @Override
    public void deleted(Consumer<E> tConsumer) {
        TransferUserRelationExample example = new TransferUserRelationExample();
        tConsumer.accept((E) example);
        TransferUserRelation.Update relation = new TransferUserRelation.Update();
        relation.setIsDeleted(FruitDict.Systems.Y.name());
        mapper.updateByExampleSelective(relation, example);
    }
}
