package wowjoy.fruits.ms.module.user;


import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.Date;

/**
 * Created by wangziwen on 2017/8/24.
 */
public class FruitUser extends AbstractEntity {
    public FruitUser() {
        this(null);
    }

    public FruitUser(String userId) {
        this.userId = userId;
        this.setUserSex(FruitDict.Systems.MAN.name());
    }

    private String userId;
    private String userName;
    private String status;
    private Date birthday;
    private String userSex;
    private String userEmail;
    private String jobTitle;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserSex() {
        return userSex;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public static FruitUserDao getDao() {
        return new FruitUserDao();
    }

    public static FruitUserVo getVo() {
        return new FruitUserVo();
    }

    public static FruitUserEmpty newEmpty(String msg) {
        return new FruitUserEmpty(msg);
    }

    public static FruitUser getInstance() {
        return new FruitUser();
    }

    public static class Info extends FruitUser {
        public Info() {
            setUuid(null);
            setIsDeleted(null);
            setUserSex(null);
        }
    }

    public static Info newInfo() {
        return new Info();
    }
}
