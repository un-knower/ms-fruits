package wowjoy.fruits.ms.module.list;

import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.List;
import java.util.Map;

/**
 * Created by wangziwen on 2017/10/17.
 */
public class FruitListVo extends FruitList {
    private String uuidVo;

    private Map<FruitDict.Systems, List<String>> projectRelation;

    public Map<FruitDict.Systems, List<String>> getProjectRelation() {
        return projectRelation;
    }

    public void setProjectRelation(Map<FruitDict.Systems, List<String>> projectRelation) {
        this.projectRelation = projectRelation;
    }

    public String getUuidVo() {
        return uuidVo;
    }

    public void setUuidVo(String uuidVo) {
        this.uuidVo = uuidVo;
    }
}
