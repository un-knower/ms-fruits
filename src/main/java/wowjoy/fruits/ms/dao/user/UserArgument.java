package wowjoy.fruits.ms.dao.user;

import wowjoy.fruits.ms.dao.InterfaceArgument;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.module.user.entity.FruitUser;

import java.util.List;
import java.util.Map;

/**
 * Created by wangziwen on 2017/8/28.
 */
public class UserArgument extends FruitUser implements InterfaceArgument {
    private AbstractUser abstractUser;

    public <T> UserArgument setDataSource(Class<T> tClass) {
        this.abstractUser = (AbstractUser) this.findContext(tClass);
        return this;
    }

    public AbstractUser getAbstractUser() {
        if (abstractUser == null)
            throw new RuntimeException("没有选择数据模型");
        return abstractUser;
    }

    public static UserArgument getInstance(Map data) {
        UserArgument result = new UserArgument();
        result.convert(data);
        return result;
    }

    /*********
     * PUBLIC *
     *********/
    public List<FruitUser> findByUser() {
        return this.getAbstractUser().findByUser(this);
    }
}
