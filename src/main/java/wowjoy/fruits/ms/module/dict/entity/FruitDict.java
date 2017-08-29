package wowjoy.fruits.ms.module.dict.entity;

import wowjoy.fruits.ms.util.AbstractEntity;

/**
 * Created by wangziwen on 2017/8/24.
 */
public class FruitDict extends AbstractEntity {

    private String dictParentCode;
    private String dictCode;
    private String dictName;

    public void setDictParentCode(String dictParentCode) {
        this.dictParentCode = dictParentCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getDictParentCode() {
        return dictParentCode;
    }

    public String getDictCode() {
        return dictCode;
    }

    public String getDictName() {
        return dictName;
    }

    /**
     * dict 管理项目所有父节点，便于父节点的管理和维护。
     */
    public enum Dict {
        PROJECT("项目"),
        PLAN("计划"),
        TASK("任务");

        private String value;

        private void setValue(String value){
            this.value = value;
        }

        public String getValue(){
            return value;
        }
        Dict(String value) {
            this.setValue(value);
        }
    }
}
