package wowjoy.fruits.ms.dao.logs;

import com.google.common.collect.Lists;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.logs.FruitLogsDao;
import wowjoy.fruits.ms.module.logs.FruitLogsExample;
import wowjoy.fruits.ms.module.logs.FruitLogsVo;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toMap;

/**
 * Created by wangziwen on 2017/8/25.
 */
public abstract class AbstractDaoLogs implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/
    protected abstract void insert(FruitLogsDao dao);

    public abstract List<FruitLogsDao> findExample(Consumer<FruitLogsExample> exampleUnaryOperator);

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public void insert(FruitLogsVo vo) {
        FruitLogsDao dao = FruitLogs.getDao();
        dao.setUuid(vo.getUuid());
        dao.setUserId(vo.getUserId());
        dao.setFruitUuid(vo.getFruitUuid());
        dao.setFruitType(vo.getFruitType());
        dao.setOperateType(vo.getOperateType());
        dao.setJsonObject(vo.getJsonObject());
        dao.setVoObject(vo.getVoObject());
        this.insert(dao);
    }

    public Map<String, LinkedList<FruitLogsDao>> findLogs(Consumer<FruitLogsExample> exampleConsumer, FruitDict.Parents parents) {
        return this.findLogs(exampleConsumer, (logs, template) -> template, parents);
    }

    public Map<String, LinkedList<FruitLogsDao>> findLogs(Consumer<FruitLogsExample> exampleConsumer,
                                                          TemplateFunction<FruitLogsDao, String> template,
                                                          FruitDict.Parents parents) {
        LogsTemplate logsTemplate = LogsTemplate.newInstance(parents);
        Map<String, LinkedList<FruitLogsDao>> logs = findExample(exampleConsumer)
                .stream()
                .map(log -> logsTemplate.msg(log, template))
                .collect(toMap(
                        FruitLogsDao::getFruitUuid,
                        log -> {
                            LinkedList<FruitLogsDao> logValue = Lists.newLinkedList();
                            logValue.add(log);
                            return logValue;
                        },
                        (newV, oldV) -> {
                            oldV.addAll(newV);
                            return oldV;
                        })
                );
        logs.forEach((k, v) -> v.sort(Comparator.comparing(AbstractEntity::getCreateDateTime).reversed()));
        return logs;
    }

}
