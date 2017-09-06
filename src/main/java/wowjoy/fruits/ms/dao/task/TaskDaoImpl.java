package wowjoy.fruits.ms.dao.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.module.task.FruitTask;
import wowjoy.fruits.ms.module.task.mapper.FruitTaskMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

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
    public void insert() {
        /*插入任务*/
        taskMapper.insertSelective(this.getTask());
    }

    @Override
    public List<FruitTask> findByUser() {
        return taskMapper.selectByUser();
    }
}
