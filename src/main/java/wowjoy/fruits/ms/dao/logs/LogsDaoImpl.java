package wowjoy.fruits.ms.dao.logs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.logs.FruitLogsDao;
import wowjoy.fruits.ms.module.logs.FruitLogsExample;
import wowjoy.fruits.ms.module.logs.mapper.FruitLogsMapper;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by wangziwen on 2017/8/25.
 */
@Service
@Transactional
public class LogsDaoImpl extends AbstractDaoLogs {

    @Autowired
    private FruitLogsMapper mapper;

    /***
     * 日志记录
     */
    @Override
    public void insert(Consumer<FruitLogsDao> daoConsumer) {
        FruitLogsDao dao = FruitLogs.getDao();
        daoConsumer.accept(dao);
        mapper.insertSelective(dao);
    }

    @Override
    public List<FruitLogsDao> findExample(Consumer<FruitLogsExample> exampleUnaryOperator) {
        FruitLogsExample example = new FruitLogsExample();
        exampleUnaryOperator.accept(example);
        return mapper.joinUserByExample(example);
    }
}
