package wowjoy.fruits.ms.dao.relation;

import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

/**
 * Created by wangziwen on 2017/8/30.
 */
public abstract class RelationBuilder {
    private AbstractEntity abstractEntity;

    public void setAbstractEntity(AbstractEntity abstractEntity) {
        this.abstractEntity = abstractEntity;
    }

    public AbstractEntity getAbstractEntity() {
        return this.abstractEntity;
    }

    abstract void insert();

    abstract void update();

    abstract void deleted();

    protected <T> T getContext(Class<T> tClass) {
        return ApplicationContextUtils.getContext().getBean(tClass);
    }
}
