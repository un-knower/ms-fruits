package wowjoy.fruits.ms.module.project;


import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.team.FruitTeam;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;

import java.util.Date;
import java.util.List;

/**
 * Created by wangziwen on 2017/8/24.
 */
public class FruitProject extends AbstractEntity {
    private String title;
    private Date predictEndDate;
    private Date endDateTime;
    private String projectStatus;

    public void setPredictEndDate(Date predictEndDate) {
        this.predictEndDate = predictEndDate;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public Date getPredictEndDate() {
        return predictEndDate;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public String getTitle() {
        return title;
    }

    /***********************
     * 静态工厂函数-创建实例  *
     ***********************/
    public static FruitProject getInstance() {
        return new FruitProject();
    }

    public static FruitProjectDao getProjectDao() {
        return new FruitProjectDao();
    }

    public static FruitProjectVo getProjectVo() {
        return new FruitProjectVo();
    }

    public static FruitProject newEmpty(String msg) {
        return new FruitProjectEmpty(msg);
    }
}
