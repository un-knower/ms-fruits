package wowjoy.fruits.ms.dao.task;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.AbstractDaoChain;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.project.FruitProjectDao;
import wowjoy.fruits.ms.module.task.FruitTask;
import wowjoy.fruits.ms.module.task.FruitTaskDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

/**
 * Created by wangziwen on 2017/11/27.
 */
public class TaskDaoNode extends AbstractDaoChain {
    private AbstractDaoTask taskDao = ApplicationContextUtils.getContext().getBean(TaskDaoImpl.class);

    public TaskDaoNode(FruitDict.Parents type) {
        super(type);
    }

    @Override
    public AbstractEntity find(String uuid) {
        if (!super.type.name().equals(FruitDict.Parents.TASK.name()))
            return super.getNext().find(uuid);
        if (StringUtils.isBlank(uuid))
            return null;
        FruitTaskDao dao = FruitTask.getDao();
        dao.setUuid(uuid);
        FruitTask result = taskDao.find(dao);
        if (!result.isNotEmpty()) return null;
        return result;
    }
}
