package wowjoy.fruits.ms.module.mark;

import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.EntityUtils;

public class UserMarkProject extends AbstractEntity {
    private String userId;

    private String projectId;

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

    public static class Insert extends UserMarkProject implements EntityUtils {
        public Insert() {
            this.setUuid(obtainUUID());
        }
    }

    public static class Update extends UserMarkProject implements EntityUtils{
        public Update() {
            this.setUuid(null);
        }
    }
}