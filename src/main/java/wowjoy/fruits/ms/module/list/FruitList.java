package wowjoy.fruits.ms.module.list;


import wowjoy.fruits.ms.module.AbstractEntity;

/**
 * Created by wangziwen on 2017/8/29.
 */
public class FruitList extends AbstractEntity {
    private String title;
    private transient String lType;

    public String getlType() {
        return lType;
    }

    public void setlType(String lType) {
        this.lType = lType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static FruitListDao getDao() {
        return new FruitListDao();
    }

    public static FruitListVo getVo() {
        return new FruitListVo();
    }

    public static FruitListEmpty getEmpty() {
        return new FruitListEmpty();
    }
}
