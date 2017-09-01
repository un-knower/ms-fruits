package wowjoy.fruits.ms.module.list.relation;


import wowjoy.fruits.ms.module.AbstractEntity;

/**
 * Created by wangziwen on 2017/8/29.
 */
public class ListUserRelation extends AbstractEntity {
    private String listId;
    private String userId;

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
