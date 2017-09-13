package wowjoy.fruits.ms.controller.vo;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;

import java.util.List;

/**
 * Created by wangziwen on 2017/9/12.
 */
public class FruitProjectVo extends FruitProject {
    private String uuidVo;
    private List<ProjectTeamRelation> teamVo;
    private List<UserProjectRelation> userVo;

    public void setUuidVo(String uuidVo) {
        this.uuidVo = uuidVo;
    }

    public String getUuidVo() {
        return uuidVo;
    }

    public List<ProjectTeamRelation> getTeamVo() {
        return teamVo;
    }

    public void setTeamVo(List<ProjectTeamRelation> teamVo) {
        this.teamVo = teamVo;
    }

    public List<UserProjectRelation> getUserVo() {
        return userVo;
    }

    public void setUserVo(List<UserProjectRelation> userVo) {
        this.userVo = userVo;
    }

    public boolean isNullTeamVo() {
        return this.getTeamVo() != null && this.getTeamVo().size() > 0 ? false : true;
    }

    public boolean isNullUserVo() {
        return this.getUserVo() != null && this.getUserVo().size() > 0 ? false : true;
    }

    public static FruitProjectVo getInstance() {
        return new FruitProjectVo();
    }

}
