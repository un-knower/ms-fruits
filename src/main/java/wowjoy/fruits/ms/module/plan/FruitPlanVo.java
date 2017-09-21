package wowjoy.fruits.ms.module.plan;

import wowjoy.fruits.ms.module.relation.entity.PlanUserRelation;
import wowjoy.fruits.ms.module.user.FruitUser;

import java.util.List;

import java.util.Date;

/**
 * Created by wangziwen on 2017/9/15.
 */
public class FruitPlanVo extends FruitPlan {
    private String uuidVo;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private Date startDateVo;
    private Date endDateVo;
    private List<PlanUserRelation> userRelation;

    public List<PlanUserRelation> getUserRelation() {
        return userRelation;
    }

    public void setUserRelation(List<PlanUserRelation> userRelation) {
        this.userRelation = userRelation;
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
