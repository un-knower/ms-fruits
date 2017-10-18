package wowjoy.fruits.ms.dao.relation;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;

/**
 * Created by wangziwen on 2017/9/12.
 */
public abstract class AbstractDaoRelation<T> implements InterfaceDao {
    protected final String checkMsg = "关联【{0}】参数错误，请检查入参。";

    /**
     * 单条记录删除
     *
     * @param relation
     */
    public abstract void insert(T relation);

    /**
     * 采用物理删除 或者 物理删除
     * <p>
     * 基本采用物理删除，有特殊需求采用逻辑删除
     *
     * @param relation
     */
    public abstract void remove(T relation);

    /**
     * 仅用于实现类。内部类尽量采用static，非静态类不利于垃圾回收。
     */
    protected static class CheckRelationException extends CheckException {
        public CheckRelationException(String message) {
            super("【Relation Exception】" + message);
        }
    }

}
