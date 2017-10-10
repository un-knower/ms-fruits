package wowjoy.fruits.ms.module.relation.entity;

import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

public class ProjectTeamRelation extends AbstractEntity {

    public static ProjectTeamRelation newInstance(String projectId, String teamId) {
        final ProjectTeamRelation result = new ProjectTeamRelation();
        result.setProjectId(projectId);
        result.setTeamId(teamId);
        return result;
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

    public void checkTpRole() {
        try {
            FruitDict.UserProjectDict.valueOf(this.getTpRole());
        } catch (Exception ex) {
            throw new CheckEntityException("后验条件错误.【团队-项目】中担任角色不存在.");
        }
    }

    public static ProjectTeamRelationVo newVo(String projectId, String teamId) {
        ProjectTeamRelationVo result = new ProjectTeamRelationVo();
        result.setProjectId(projectId);
        result.setTeamId(teamId);
        return result;
    }


}