package wowjoy.fruits.ms.module.relation.entity;

import wowjoy.fruits.ms.module.AbstractEntity;

/**
 * Created by wangziwen on 2017/9/5.
 */
public class UserTeamRelation extends AbstractEntity {
    public UserTeamRelation() {
    }

    public UserTeamRelation(String userId, String teamId, String utRole) {
        this.userId = userId;
        this.teamId = teamId;
        this.utRole = utRole;
    }

    private String userId;
    private String teamId;
    private String utRole;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getUtRole() {
        return utRole;
    }

    public void setUtRole(String utRole) {
        this.utRole = utRole;
    }
}
