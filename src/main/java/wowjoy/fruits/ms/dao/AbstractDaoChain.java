package wowjoy.fruits.ms.dao;

import wowjoy.fruits.ms.dao.list.ListDaoNode;
import wowjoy.fruits.ms.dao.plan.PlanDaoNode;
import wowjoy.fruits.ms.dao.project.ProjectDaoNode;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

/**
 * Created by wangziwen on 2017/11/27.
 * 通过责任链的方式，遍历符合需求的节点
 */
public abstract class AbstractDaoChain {

    private AbstractDaoChain next;
    protected final FruitDict.Parents type;

    public AbstractDaoChain(FruitDict.Parents type) {
        this.type = type;
    }

    public AbstractDaoChain setNext(AbstractDaoChain next) {
        this.next = next;
        return next;
    }

    public AbstractDaoChain getNext() {
        return next == null ? new EmptyNode(type) : next;
    }

    public abstract AbstractEntity find(String uuid);

    public static AbstractDaoChain newInstance(FruitDict.Parents type) {
        PlanDaoNode planDaoNode = new PlanDaoNode(type);
        ListDaoNode listDaoNode = new ListDaoNode(type);
        ProjectDaoNode projectDaoNode = new ProjectDaoNode(type);
        planDaoNode.setNext(listDaoNode).setNext(projectDaoNode);
        return planDaoNode;
    }

    private class EmptyNode extends AbstractDaoChain {
        public EmptyNode(FruitDict.Parents type) {
            super(type);
        }

        @Override
        public AbstractEntity find(String uuid) {
            return null;
        }
    }
}
