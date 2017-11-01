package wowjoy.fruits.ms.module.user;

import wowjoy.fruits.ms.module.AbstractEntity;

public class FruitAccount extends AbstractEntity {

    private String principal;

    private String credentials;

    private String type;

    private String status;

    private String userId;

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static FruitAccountDao getDao() {
        return new FruitAccountDao();
    }

    public static FruitAccountVo getVo() {
        return new FruitAccountVo();
    }
}