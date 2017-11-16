package wowjoy.fruits.ms.module.list;

import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.List;
import java.util.Map;

/**
 * Created by wangziwen on 2017/10/17.
 */
public class FruitListVo extends FruitList {
    private String uuidVo;

    private Map<FruitDict.Systems, List<String>> listRelation;

    public Map<FruitDict.Systems, List<String>> getListRelation() {
        return listRelation;
    }

    public void setListRelation(Map<FruitDict.Systems, List<String>> listRelation) {
        this.listRelation = listRelation;
    }

    public String getUuidVo() {

        return uuidVo;
    }

    public void setUuidVo(String uuidVo) {
        this.uuidVo = uuidVo;
    }
}
