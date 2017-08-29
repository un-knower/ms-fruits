package wowjoy.fruits.ms.dao.dict;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import wowjoy.fruits.ms.module.dict.entity.FruitDict;
import wowjoy.fruits.ms.module.plan.entity.FruitPlan;
import wowjoy.fruits.ms.module.project.entity.FruitProject;
import wowjoy.fruits.ms.module.task.entity.FruitTask;
import wowjoy.fruits.ms.util.CommonEnum;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by wangziwen on 2017/8/24.
 */
@Service
public class DataDictDaoImpl extends AbstractDict {

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
        for (Field field : CommonEnum.class.getFields()) {
            FruitDict data = new FruitDict();
            data.setDictCode(field.getName());
            data.setDictName(field.getName());
            this.addData(data);
        }
        return this;
    }

    private DataDictDaoImpl planEnum() {
        /*计划*/
        for (FruitPlan.Dict fruitPlan : FruitPlan.Dict.class.getEnumConstants()) {
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
        for (FruitProject.Dict dict : FruitProject.Dict.class.getEnumConstants()) {
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
        for (FruitTask.Dict dict : FruitTask.Dict.class.getEnumConstants()) {
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

    @Override
    public List<FruitDict> find() {
        return this.commonEnum().planEnum().taskEnum().projectEnum().getData();
    }

}
