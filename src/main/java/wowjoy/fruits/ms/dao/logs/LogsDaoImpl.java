package wowjoy.fruits.ms.dao.logs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.module.logs.FruitLogsDao;
import wowjoy.fruits.ms.module.logs.FruitLogsExample;
import wowjoy.fruits.ms.module.logs.mapper.FruitLogsMapper;
import wowjoy.fruits.ms.module.plan.example.FruitPlanExample;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

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
     * @param dao
     */
    @Override
    public void insert(FruitLogsDao dao) {
        mapper.insertSelective(dao);
    }

    @Override
    public List<FruitLogsDao> findExample(Consumer<FruitLogsExample> exampleUnaryOperator){
        FruitLogsExample example = new FruitLogsExample();
        exampleUnaryOperator.accept(example);
        return mapper.joinUserByExample(example);
    }
}
