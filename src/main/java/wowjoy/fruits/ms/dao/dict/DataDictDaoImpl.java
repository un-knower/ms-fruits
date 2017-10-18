package wowjoy.fruits.ms.dao.dict;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by wangziwen on 2017/8/24.
 */
@Service
public class DataDictDaoImpl extends AbstractDaoDict {

    private List<FruitDict> data;

    private void addData(FruitDict dict) {
        this.getData().add(dict);
    }

    private List<FruitDict> getData() {
        if (data == null)
            data = Lists.newLinkedList();
        return data;
    }

    /**
     * Data
     */

    private DataDictDaoImpl commonEnum() {
        /*基础*/
        for (Field field : FruitDict.Dict.class.getFields()) {
            FruitDict data = new FruitDict();
            data.setDictCode(field.getName());
            data.setDictName(field.getName());
            this.addData(data);
        }
        return this;
    }

    private DataDictDaoImpl planEnum() {
        /*计划*/
        for (FruitDict.PlanDict fruitPlan : FruitDict.PlanDict.class.getEnumConstants()) {
            FruitDict data = new FruitDict();
            data.setDictParentCode(fruitPlan.getParentCode());
            data.setDictCode(fruitPlan.name());
            data.setDictName(fruitPlan.getValue());
            this.addData(data);
        }
        return this;
    }

    private DataDictDaoImpl projectEnum() {
        /*项目*/
        for (FruitDict.ProjectDict dict : FruitDict.ProjectDict.class.getEnumConstants()) {
            FruitDict data = new FruitDict();
            data.setDictParentCode(dict.getParentCode());
            data.setDictCode(dict.name());
            data.setDictName(dict.getValue());
            this.addData(data);
        }
        return this;
    }

    private DataDictDaoImpl taskEnum() {
        /*任务*/
        for (FruitDict.TaskDict dict : FruitDict.TaskDict.class.getEnumConstants()) {
            FruitDict data = new FruitDict();
            data.setDictParentCode(dict.getParentCode());
            data.setDictCode(dict.name());
            data.setDictName(dict.getValue());
            this.addData(data);
        }
        return this;
    }

    private DataDictDaoImpl milestoneEnum() {
        /*里程碑*/
        for (FruitDict.MilestoneDict dict : FruitDict.MilestoneDict.class.getEnumConstants()) {
            FruitDict data = new FruitDict();
            data.setDictParentCode(dict.getParentCode());
            data.setDictCode(dict.name());
            data.setDictName(dict.getValue());
            this.addData(data);
        }
        return this;
    }

    private DataDictDaoImpl taskUserEnum() {
        /*任务-用户*/
        for (FruitDict.TaskUserDict dict : FruitDict.TaskUserDict.class.getEnumConstants()) {
            FruitDict data = new FruitDict();
            data.setDictParentCode(dict.getParentCode());
            data.setDictCode(dict.name());
            data.setDictName(dict.getValue());
            this.addData(data);
        }
        return this;
    }

    private DataDictDaoImpl planUserEnum() {
        /*计划-用户*/
        for (FruitDict.PlanUserDict dict : FruitDict.PlanUserDict.class.getEnumConstants()) {
            FruitDict data = new FruitDict();
            data.setDictParentCode(dict.getParentCode());
            data.setDictCode(dict.name());
            data.setDictName(dict.getValue());
            this.addData(data);
        }
        return this;
    }


    /**
     * PUBLIC
     */

    public List<FruitDict> find() {
        return this.commonEnum().planEnum().taskEnum().projectEnum().milestoneEnum().taskUserEnum().planUserEnum().getData();
    }

}
