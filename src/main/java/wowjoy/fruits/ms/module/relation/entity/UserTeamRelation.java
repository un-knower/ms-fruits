package wowjoy.fruits.ms.module.relation.entity;

import wowjoy.fruits.ms.module.AbstractEntity;

public class UserTeamRelation extends AbstractEntity {

    private String userId;

    private String teamId;

    private String utRole;

    public static class Update extends UserTeamRelation {
        public Update() {
            setUuid(null);
        }
    }

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

    public static UserTeamRelation newInstance(final String userId, final String teamId, final String utRole) {
        final UserTeamRelation relation = new UserTeamRelation();
        relation.setTeamId(teamId);
        relation.setUserId(userId);
        relation.setUtRole(utRole);
        return relation;
    }

    public static UserTeamRelation newInstance(final String teamId) {
        return newInstance(null, teamId, null);
    }

    public static UserTeamRelation getInstance() {
        return new UserTeamRelation();
    }
}