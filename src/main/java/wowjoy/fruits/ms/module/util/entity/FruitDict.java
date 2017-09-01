package wowjoy.fruits.ms.module.util.entity;


import wowjoy.fruits.ms.module.AbstractEntity;

/**
 * Created by wangziwen on 2017/8/24.
 * 数据字典
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
        /**
         * 业务字典
         */
        PROJECT("项目"),
        PLAN("计划"),
        PLANUSER("计划-用户"),
        TASK("任务"),
        TASKUSER("任务-用户"),
        MEILESTONE("里程碑"),

        /**
         * 系统字典
         */
        N("NO"),

        Y("YES"),

        MAN("男"),

        GIRLS("女");

        private String value;

        private void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        Dict(String value) {
            this.setValue(value);
        }
    }

    /**
     * plan 状态管理
     */
    public enum PlanDict {
        //进行中
        PENDING("进行中"),
        //准时完成
        COMPLETE("准时完成"),
        //延期完成
        OUTTIMECOMPLET("延期完成"),
        //终止
        END("以终止");

        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }

        PlanDict(String value) {
            this.setParentCode(Dict.PLAN.name());
            this.setValue(value);
        }
    }

    /**
     * project 状态管理
     */
    public enum ProjectDict {
        //进行中
        UNDERWAY("进行中"),
        //暂停
        STOP("已暂停"),
        //终止
        END("已终止"),
        //已完成
        COMPLETE("已完成");

        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }


        ProjectDict(String value) {
            this.setParentCode(Dict.PROJECT.name());
            this.setValue(value);
        }
    }

    /**
     * task 状态管理
     */
    public enum TaskDict {
        /**
         * 任务优先级
         */
        LOW("低"),
        CENTRE("中"),
        HEIGH("高"),
        /**
         * 任务状态
         */
        START("进行中"),
        END("结束");

        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }

        TaskDict(String value) {
            this.setParentCode(Dict.TASK.name());
            this.setValue(value);
        }
    }

    /**
     * milestone 里程碑管理
     */
    public enum MilestoneDict {
        //未完成
        UNDONE("未完成"),
        //已完成
        COMPLETE("已完成");


        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }

        MilestoneDict(String value) {
            this.setParentCode(Dict.MEILESTONE.name());
            this.setValue(value);
        }
    }

    /************
     * relation *
     ************/

    /**
     * taskUserRelation 任务用户关联字典管理
     */
    public enum TaskUserDict {
        PRINCIPAL("负责人"),
        PARTICIPANT("参与者");


        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }

        TaskUserDict(String value) {
            this.setParentCode(Dict.TASKUSER.name());
            this.setValue(value);
        }
    }

    /**
     * planUserRelation 计划用户关联字典管理
     */
    public enum PlanUserDict {
        PRINCIPAL("负责人"),
        PARTICIPANT("参与者");


        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }

        PlanUserDict(String value) {
            this.setParentCode(Dict.PLANUSER.name());
            this.setValue(value);
        }
    }

}
