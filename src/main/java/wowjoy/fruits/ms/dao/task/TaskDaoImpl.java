package wowjoy.fruits.ms.dao.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.module.task.FruitTask;
import wowjoy.fruits.ms.module.task.mapper.FruitTaskMapper;

import java.util.List;

/**
 * Created by wangziwen on 2017/8/31.
 */
@Service
@Transactional
public class TaskDaoImpl extends AbstractDaoTask {
    @Autowired
    private FruitTaskMapper taskMapper;

    @Override
    public void insert(FruitTask task) {
        taskMapper.insertSelective(task);
    }

    @Override
    public List<FruitTask> findByUser() {
        return taskMapper.selectByUser();
    }
}
