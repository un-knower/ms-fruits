package wowjoy.fruits.ms.module.relation.entity;

import wowjoy.fruits.ms.module.AbstractEntity;

/**
 * Created by wangziwen on 2017/9/5.
 */
public class UserProjectRelation extends AbstractEntity {
    private String userId;
    private String projectId;
    private String upRole;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getUpRole() {
        return upRole;
    }

    public void setUpRole(String upRole) {
        this.upRole = upRole;
    }
}
