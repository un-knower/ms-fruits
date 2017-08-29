package wowjoy.fruits.ms.module.project.entity;

import wowjoy.fruits.ms.module.dict.entity.FruitDict;
import wowjoy.fruits.ms.util.AbstractEntity;

/**
 * Created by wangziwen on 2017/8/24.
 */
public class FruitProject extends AbstractEntity {
    private String title;
    private String teamStatus;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTeamStatus(String teamStatus) {
        this.teamStatus = teamStatus;
    }

    public String getTitle() {
        return title;
    }

    public String getTeamStatus() {
        return teamStatus;
    }

    /**
     * project 状态管理
     */
    public enum Dict {
        //进行中
        UNDERWAY("进行中"),
        //暂停
        STOP("以暂停"),
        //终止
        END("以终止"),
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


        Dict(String value) {
            this.setParentCode(FruitDict.Dict.PROJECT.name());
            this.setValue(value);
        }
    }
}
