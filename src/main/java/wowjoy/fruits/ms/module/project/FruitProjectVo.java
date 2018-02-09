package wowjoy.fruits.ms.module.project;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.assertj.core.util.Lists;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.*;

/**
 * Created by wangziwen on 2017/9/12.
 */
public class FruitProjectVo extends FruitProject {

    protected FruitProjectVo() {
    }

    private String uuidVo;
    private Map<FruitDict.Systems, List<ProjectTeamRelation>> teamRelation;
    private Map<FruitDict.Systems, List<UserProjectRelation>> userRelation;

    public Optional<Map<FruitDict.Systems, List<ProjectTeamRelation>>> getTeamRelation() {
        if (teamRelation != null)
            return Optional.of(teamRelation);
        return Optional.empty();
    }

    public void setTeamRelation(Map<FruitDict.Systems, List<ProjectTeamRelation>> teamRelation) {
        this.teamRelation = teamRelation;
    }

    public Optional<Map<FruitDict.Systems, List<UserProjectRelation>>> getUserRelation() {
        if (userRelation != null)
            return Optional.of(userRelation);
        return Optional.empty();
    }

    public void setUserRelation(Map<FruitDict.Systems, List<UserProjectRelation>> userRelation) {
        this.userRelation = userRelation;
    }

    private <T extends AbstractEntity> Map<String, List<T>> parset(Map<String, List<T>> relation) {
        LinkedHashMap<String, List<T>> result = Maps.newLinkedHashMap();
        ArrayList<FruitDict.Systems> parents = Lists.newArrayList(FruitDict.Systems.DELETE, FruitDict.Systems.ADD);
        parents.forEach((i) -> {
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

    @Override
    public FruitProjectVo clone() {
        return new Gson().fromJson(new Gson().toJsonTree(this).getAsJsonObject(), TypeToken.of(this.getClass()).getType());
    }
}
