package wowjoy.fruits.ms.module.relation.entity;

import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

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
        try {
            this.upRole = FruitDict.UserProjectDict.valueOf(upRole).name();
        } catch (Exception ex) {
            throw new CheckException("用户关联团队的角色不存在");
        }
    }

    public static UserProjectRelation getInstance() {
        return new UserProjectRelation();
    }

    public static UserProjectRelation newInstance(String projectId, String userId) {
        final UserProjectRelation result = new UserProjectRelation();
        result.setProjectId(projectId);
        result.setUserId(userId);
        return result;
    }

    public static UserProjectRelation newInstance(String projectId, String userId, String upRole) {
        final UserProjectRelation result = new UserProjectRelation();
        result.setProjectId(projectId);
        result.setUserId(userId);
        result.setUpRole(upRole);
        return result;
    }

}