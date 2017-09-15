package wowjoy.fruits.ms.module.project;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.commons.lang3.EnumUtils;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.List;

/**
 * Created by wangziwen on 2017/9/12.
 */
public class FruitProjectVo extends FruitProject {

    protected FruitProjectVo() {
    }

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

    public void checkStatus() {
        if (!EnumUtils.isValidEnum(FruitDict.ProjectDict.class, this.getProjectStatus()))
            throw new CheckEntityException("【用户-项目】角色不存在.");
    }

    @Override
    public FruitProjectVo clone() {
        return new Gson().fromJson(new Gson().toJsonTree(this).getAsJsonObject(), TypeToken.of(this.getClass()).getType());
    }
}
