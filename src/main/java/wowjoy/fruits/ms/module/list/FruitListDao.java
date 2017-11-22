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

    private Map<FruitDict.Systems, List<String>> projectRelation;

    private List<FruitTaskDao> tasks;

    public List<FruitTaskDao> getTasks() {
        if (tasks == null)
            tasks = Lists.newLinkedList();
        return tasks;
    }

    public void setTasks(List<FruitTaskDao> tasks) {
        this.tasks = tasks;
    }

    public List<String> getProjectRelation(FruitDict.Systems systems) {
        return projectRelation != null && projectRelation.containsKey(systems) ? projectRelation.get(systems) : Lists.newLinkedList();
    }

    public void setProjectRelation(Map<FruitDict.Systems, List<String>> projectRelation) {
        this.projectRelation = projectRelation;
    }
}
