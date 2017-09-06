package wowjoy.fruits.ms.dao.relation;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.user.FruitUser;

import java.util.List;

/**
 * Created by wangziwen on 2017/9/5.
 */
public abstract class AbstractDaoRelation implements InterfaceDao {
    private AbstractEntity abstractEntity;

    protected AbstractEntity getAbstractEntity(){
        return abstractEntity;
    }

    public AbstractDaoRelation setAbstractEntity(AbstractEntity abstractEntity) {
        this.abstractEntity = abstractEntity;
        return this;
    }

    public abstract List<FruitUser> findByEntity();
}
