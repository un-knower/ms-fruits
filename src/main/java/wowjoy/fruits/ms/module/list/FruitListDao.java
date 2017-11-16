package wowjoy.fruits.ms.module.list;

import com.google.common.collect.Lists;
import wowjoy.fruits.ms.module.task.FruitTaskDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.List;
import java.util.Map;

/**
 * Created by wangziwen on 2017/10/17.
 */
public class FruitListDao extends FruitList {
    public FruitListDao() {
        setUuid(null);
    }

    private Map<FruitDict.Systems, List<String>> listRelation;

    private List<FruitTaskDao> tasks;

    public List<FruitTaskDao> getTasks() {
        if (tasks == null)
            tasks = Lists.newLinkedList();
        return tasks;
    }

    public void setTasks(List<FruitTaskDao> tasks) {
        this.tasks = tasks;
    }

    public List<String> getListRelation(FruitDict.Systems systems) {
        return listRelation != null && listRelation.containsKey(systems) ? listRelation.get(systems) : Lists.newLinkedList();
    }

    public void setListRelation(Map<FruitDict.Systems, List<String>> listRelation) {
        this.listRelation = listRelation;
    }
}
