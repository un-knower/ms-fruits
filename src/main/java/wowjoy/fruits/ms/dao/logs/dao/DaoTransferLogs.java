package wowjoy.fruits.ms.dao.logs.dao;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.logs.service.ServiceTransferLogs;
import wowjoy.fruits.ms.dao.relation.impl.RelationTransfer;
import wowjoy.fruits.ms.module.logs.transfer.FruitTransferLogs;
import wowjoy.fruits.ms.module.logs.transfer.FruitTransferLogsExample;
import wowjoy.fruits.ms.module.logs.transfer.FruitTransferUser;
import wowjoy.fruits.ms.module.logs.transfer.mapper.FruitTransferLogsMapper;
import wowjoy.fruits.ms.module.relation.entity.TransferUserRelation;
import wowjoy.fruits.ms.module.relation.example.TransferUserRelationExample;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.ArrayList;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

/**
 * Created by wangziwen on 2018/3/9.
 */
@Service
@Transactional
public class DaoTransferLogs extends ServiceTransferLogs {
    private final FruitTransferLogsMapper transferLogsMapper;
    private final RelationTransfer<TransferUserRelation, TransferUserRelationExample> relationTransfer;

    @Autowired
    public DaoTransferLogs(FruitTransferLogsMapper transferLogsMapper, RelationTransfer<TransferUserRelation, TransferUserRelationExample> relationTransfer) {
        this.transferLogsMapper = transferLogsMapper;
        this.relationTransfer = relationTransfer;
    }

    @Override
    public void insert(Consumer<FruitTransferLogs.Insert> insertConsumer) {
        FruitTransferLogs.Insert insert = FruitTransferLogs.newInsert();
        insertConsumer.accept(insert);
        transferLogsMapper.insertSelective(insert);
        ArrayList<TransferUserRelation> transferUserList = Lists.newArrayList();
        transferUserList.addAll(insert.getTransferUserRelation(FruitDict.TransferDict.NEW)
                .orElse(Lists.newArrayList())
                .parallelStream()
                .map(transferUserRelation -> transferUserRelation.setTransferId(insert.getUuid()).setStatus(FruitDict.TransferDict.NEW.name()))
                .collect(toList()));
        transferUserList.addAll(insert.getTransferUserRelation(FruitDict.TransferDict.OLD)
                .orElse(Lists.newArrayList())
                .parallelStream()
                .map(transferUserRelation -> transferUserRelation.setTransferId(insert.getUuid()).setStatus(FruitDict.TransferDict.OLD.name()))
                .collect(toList()));
        transferUserList.parallelStream().forEach(transferUserRelation -> relationTransfer.insert(template -> {
            template.setTransferId(transferUserRelation.getTransferId());
            template.setUserId(transferUserRelation.getUserId());
            template.setStatus(transferUserRelation.getStatus());
        }));
    }

    @Override
    public ArrayList<FruitTransferLogs> findByExample(Consumer<FruitTransferLogsExample> exampleConsumer) {
        FruitTransferLogsExample example = new FruitTransferLogsExample();
        exampleConsumer.accept(example);
        return transferLogsMapper.selectByExample(example).stream().collect(toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<FruitTransferUser> findUserByTransferId(ArrayList<String> transferIds) {
        return transferLogsMapper.selectUserByTransferId(transferIds);
    }

}
