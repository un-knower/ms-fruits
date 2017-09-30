package wowjoy.fruits.ms.module.project;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.commons.lang3.EnumUtils;
import org.assertj.core.util.Lists;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangziwen on 2017/9/12.
 */
public class FruitProjectVo extends FruitProject {

    protected FruitProjectVo() {
    }

    private String uuidVo;
    private Map<String, List<ProjectTeamRelation>> teamRelation;
    private Map<String, List<UserProjectRelation>> userRelation;

    public Map<String, List<ProjectTeamRelation>> getTeamRelation() {
        return parset(teamRelation);
    }

    public void setTeamRelation(Map<String, List<ProjectTeamRelation>> teamRelation) {
        this.teamRelation = teamRelation;
    }

    public Map<String, List<UserProjectRelation>> getUserRelation() {
        return parset(userRelation);
    }

    public void setUserRelation(Map<String, List<UserProjectRelation>> userRelation) {
        this.userRelation = userRelation;
    }

    private <T extends AbstractEntity> Map<String, List<T>> parset(Map<String, List<T>> relation) {
        LinkedHashMap<String, List<T>> result = Maps.newLinkedHashMap();
        ArrayList<FruitDict.Dict> dicts = Lists.newArrayList(FruitDict.Dict.DELETE, FruitDict.Dict.ADD);
        dicts.forEach((i) -> {
            if (relation.containsKey(i.name().toLowerCase()))
                result.put(i.name().toLowerCase(), relation.get(i.name().toLowerCase()));
        });
        return result;
    }

    public void setUuidVo(String uuidVo) {
        this.uuidVo = uuidVo;
    }

    public String getUuidVo() {
        return uuidVo;
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
