package wowjoy.fruits.ms.module.user;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by wangziwen on 2017/9/14.
 */
public class FruitUserDao extends FruitUser {
    protected FruitUserDao() {
        setUuid(null);
    }

    private String projectRole;

    private String planRole;

    private String principal;

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    private List<FruitAccountDao> accounts;

    public List<FruitAccountDao> getAccounts() {
        return accounts == null ? (accounts = Lists.newLinkedList()) : accounts;
    }

    public void setAccounts(List<FruitAccountDao> accounts) {
        accounts = accounts;
    }

    public String getPlanRole() {
        return planRole;
    }

    public void setPlanRole(String planRole) {
        this.planRole = planRole;
    }

    public String getProjectRole() {
        return projectRole;
    }

    public void setProjectRole(String projectRole) {
        this.projectRole = projectRole;
    }
}
