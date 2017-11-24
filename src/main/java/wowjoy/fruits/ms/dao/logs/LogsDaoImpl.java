package wowjoy.fruits.ms.dao.logs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.module.logs.FruitLogsDao;
import wowjoy.fruits.ms.module.logs.mapper.FruitLogsMapper;

/**
 * Created by wangziwen on 2017/8/25.
 */
@Service
@Transactional
public class LogsDaoImpl extends AbstractDaoLogs {

    @Autowired
    private FruitLogsMapper mapper;

    /***
     * 日志记 录
     * @param dao
     */
    @Override
    public void insert(FruitLogsDao dao) {
        mapper.insertSelective(dao);
    }

}
