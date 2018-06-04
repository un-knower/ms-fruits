package wowjoy.fruits.ms.dao.logs.service;

import com.google.common.collect.Lists;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.logs.transfer.FruitTransferLogs;
import wowjoy.fruits.ms.module.logs.transfer.FruitTransferLogsExample;
import wowjoy.fruits.ms.module.logs.transfer.FruitTransferUser;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;

/**
 * Created by wangziwen on 2018/3/9.
 */
public abstract class ServiceTransferLogs implements InterfaceDao {
    public abstract void insert(Consumer<FruitTransferLogs.Insert> insertConsumer);

    protected abstract ArrayList<FruitTransferLogs> findByExample(Consumer<FruitTransferLogsExample> exampleConsumer);

    protected abstract ArrayList<FruitTransferUser> findUserByTransferId(ArrayList<String> transferIds);

    public FruitTransferLogs findInfo(String uuid) {
        Optional<FruitTransferLogs> transferOptional = findByExample(fruitTransferLogsExample -> fruitTransferLogsExample.createCriteria().andUuidEqualTo(uuid).andIsDeletedEqualTo(FruitDict.Systems.N.name())).stream().findAny();
        if (!transferOptional.isPresent())
            throw new CheckException(FruitDict.Exception.Check.TRANSFER_OBTAIN_NOT_EXISTS.name());
        transferOptional.get().setTransferUser(this.findUserByTransferId(Lists.newArrayList(uuid))
                .stream()
                .collect(groupingBy(user -> FruitDict.TransferDict.valueOf(user.getStatus()), toCollection(ArrayList::new)))
        );
        return transferOptional.get();
    }

}
