package wowjoy.fruits.ms.module.relation.entity;

import org.apache.commons.lang3.EnumUtils;
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
        this.upRole = upRole;
    }

    public static UserProjectRelation newInstance(String projectId, String userId) {
        final UserProjectRelation result = new UserProjectRelation();
        result.setProjectId(projectId);
        result.setUserId(userId);
        return result;
    }

    public void checkUpRole() {
        if (!EnumUtils.isValidEnum(FruitDict.UserProjectDict.class, this.upRole))
            throw new EntityCheckException("【用户-项目】角色不存在.");
    }
}