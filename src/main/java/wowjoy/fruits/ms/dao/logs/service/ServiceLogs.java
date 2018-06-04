package wowjoy.fruits.ms.dao.logs.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.dao.logs.template.LogsReadTemplate;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.logs.FruitLogsDao;
import wowjoy.fruits.ms.module.logs.FruitLogsExample;
import wowjoy.fruits.ms.module.logs.FruitLogsVo;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.*;
import java.util.function.Consumer;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;

/**
 * Created by wangziwen on 2017/8/25.
 */
public abstract class ServiceLogs implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/
    protected abstract void insert(Consumer<FruitLogsDao> daoConsumer);

    public abstract List<FruitLogsDao> findExample(Consumer<FruitLogsExample> exampleUnaryOperator);

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public void insertVo(Consumer<FruitLogsVo> voConsumer) {
        FruitLogsVo vo = FruitLogs.getVo();
        voConsumer.accept(vo);
        this.insert(dao -> {
            dao.setUuid(vo.getUuid());
            dao.setUserId(vo.getUserId());
            dao.setFruitUuid(vo.getFruitUuid());
            dao.setFruitType(vo.getFruitType());
            dao.setOperateType(vo.getOperateType());
            dao.setJsonObject(vo.getJsonObject());
            dao.setVoObject(vo.getVoObject());
        });
    }

    public Map<String, ArrayList<FruitLogs.Info>> findLogs(Consumer<FruitLogsExample> exampleConsumer,
                                                           FruitDict.Parents parents) {
        LogsReadTemplate logsTemplate = LogsReadTemplate.newInstance(parents);
        long startTime = System.currentTimeMillis();
        Map<String, ArrayList<FruitLogsDao>> logsMap = this.findExample(exampleConsumer).stream().collect(groupingBy(FruitLogsDao::getFruitUuid, toCollection(ArrayList::new)));
        logger.info("findLogs：" + (System.currentTimeMillis() - startTime));
        Map<String, ArrayList<FruitLogs.Info>> exportLogs = Maps.newHashMap();
        logsMap.forEach((fruitUUID, logs) -> {
            LinkedList<FruitLogs.Info> logList = Lists.newLinkedList();
            int size = logs.size() - 1;
            for (int i = size; i >= 0; i--) {
                logList.addFirst(logsTemplate.msg(Optional.of(i)
                        .filter(count -> count < size)
                        .map(count -> ++count)
                        .map(logs::get)
                        .orElse(null), logs.get(i)));
            }
            exportLogs.put(fruitUUID, new ArrayList<>(logList));
        });
        return exportLogs;
    }

}
