package wowjoy.fruits.ms.module.user;

/**
 * Created by wangziwen on 2017/10/26.
 */
public class FruitAccountDao extends FruitAccount {
    private FruitUserDao user;

    public FruitUserDao getUser() {
        return user;
    }

    public void setUser(FruitUserDao user) {
        this.user = user;
    }

    public FruitAccountDao() {
        setUuid(null);
    }
}
