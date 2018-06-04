package wowjoy.fruits.ms.module.defect;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 * 当获取综合字段所需数据时，会牵扯到关联数据，并且关联数据只要个别字段数据，没必要浪费内充空间存储关联对象
 */
public class DefectDuplicate extends FruitDefect {
    private String createUserName;
    private String handlerUserName;

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getHandlerUserName() {
        return handlerUserName;
    }

    public void setHandlerUserName(String handlerUserName) {
        this.handlerUserName = handlerUserName;
    }
}
