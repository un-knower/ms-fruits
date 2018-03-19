package wowjoy.fruits.ms.dao.relation;

import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.example.PlanUserRelationExample;

import java.util.Optional;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

/**
 * Created by wangziwen on 2017/9/12.
 */
public interface RelationInterface<T, E> {
    String checkMsg = "关联【{0}】参数错误，请检查入参。";

    /**
     * 单条记录删除
     *
     * @param relation
     */
    @Deprecated
    default void insert(T relation) {
        /*未来版本将废弃当前接口*/
    }

    /*目前不强制实现。后期将替换原插入接口*/
    default void insert(Consumer<T> tConsumer) {

    }

    /**
     * 采用物理删除
     *
     * @param relation
     */
    default void remove(T relation) {
        /*不强制实现物理删除接口*/
    }

    /**
     * 采用逻辑删除
     *
     * @param relation
     */
    @Deprecated
    default void deleted(T relation) {
        /*未来版本将废弃当前接口*/
    }

    /**
     * v2.5.0 逻辑删除
     * 当前版本支持lambda模式
     *
     * @param tConsumer
     */
    /*目前不强制实现，后期替换原逻辑删除接口*/
    default void deleted(Consumer<E> tConsumer) {

    }


    /**
     * 仅用于实现类。内部类尽量采用static，非静态类不利于垃圾回收。
     */
    class CheckRelationException extends CheckException {
        public CheckRelationException(String message) {
            super("【Relation Exception】" + message);
        }
    }

}
