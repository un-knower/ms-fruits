package wowjoy.fruits.ms.module.plan;

import com.google.common.collect.Maps;
import org.assertj.core.util.Lists;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.*;

/**
 * Created by wangziwen on 2017/9/15.
 */
public class FruitPlanVo extends FruitPlan {
    private String uuidVo;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private Date startDateVo;
    private Date endDateVo;
    /*提供年份*/
    private String year;
    /*提供月份*/
    private String month;
    private String projectId;
    private Map<FruitDict.Systems, List<String>> userRelation;
    private Map<FruitDict.Systems, List<String>> projectRelation;
    private String desc;
    private String asc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAsc() {
        return asc;
    }

    public void setAsc(String asc) {
        this.asc = asc;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Map<FruitDict.Systems, List<String>> getUserRelation() {
        return userRelation;
    }

    public void setUserRelation(Map<FruitDict.Systems, List<String>> userRelation) {
        this.userRelation = userRelation;
    }

    public Map<FruitDict.Systems, List<String>> getProjectRelation() {
        return projectRelation;
    }

    public void setProjectRelation(Map<FruitDict.Systems, List<String>> projectRelation) {
        this.projectRelation = projectRelation;
    }

    private Map<String, List<String>> parset(Map<String, List<String>> relation) {
        LinkedHashMap<String, List<String>> result = Maps.newLinkedHashMap();
        ArrayList<FruitDict.Systems> parents = Lists.newArrayList(FruitDict.Systems.DELETE, FruitDict.Systems.ADD);
        parents.forEach((i) -> {
            if (relation.containsKey(i.name().toLowerCase()))
                result.put(i.name().toLowerCase(), relation.get(i.name().toLowerCase()));
        });
        return result;
    }

    public Date getStartDateVo() {
        return startDateVo;
    }

    public void setStartDateVo(Date startDateVo) {
        this.startDateVo = startDateVo;
    }

    public Date getEndDateVo() {
        return endDateVo;
    }

    public void setEndDateVo(Date endDateVo) {
        this.endDateVo = endDateVo;
    }

    public String getUuidVo() {
        return uuidVo;
    }

    public void setUuidVo(String uuidVo) {
        this.uuidVo = uuidVo;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
