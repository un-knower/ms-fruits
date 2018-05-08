package wowjoy.fruits.ms.module.relation.entity;

import wowjoy.fruits.ms.module.AbstractEntity;

public class ProjectTeamRelation extends AbstractEntity {

    public static class Update extends ProjectTeamRelation {
        public Update() {
            setUuid(null);
        }
    }

    public static ProjectTeamRelation newInstance(String projectId, String teamId) {
        final ProjectTeamRelation result = new ProjectTeamRelation();
        result.setProjectId(projectId);
        result.setTeamId(teamId);
        return result;
    }

    public static ProjectTeamRelation newInstance(String projectId, String teamId, String tpRole) {
        final ProjectTeamRelation result = new ProjectTeamRelation();
        result.setProjectId(projectId);
        result.setTeamId(teamId);
        result.setTpRole(tpRole);
        return result;
    }

    public static ProjectTeamRelation getInstance() {
        return new ProjectTeamRelation();
    }

    private String projectId;

    private String teamId;

    private String tpRole;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTpRole() {
        return tpRole;
    }

    public void setTpRole(String tpRole) {
        this.tpRole = tpRole;
    }

}