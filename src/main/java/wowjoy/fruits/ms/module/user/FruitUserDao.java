package wowjoy.fruits.ms.module.user;

import com.google.common.collect.Lists;
import wowjoy.fruits.ms.module.notepad.FruitNotepadDao;

import java.util.List;

/**
 * Created by wangziwen on 2017/9/14.
 */
public class FruitUserDao extends FruitUser {
    protected FruitUserDao() {
        setUuid(null);
    }

    private String projectRole;

    /**
     * 计划角色
     */
    private String planRole;

    /**
     * 团队角色
     */
    private transient String teamRole;

    private String principal;

    private String taskId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    private List<FruitAccountDao> accounts;

    public String getTeamRole() {
        return teamRole;
    }

    public void setTeamRole(String teamRole) {
        this.teamRole = teamRole;
    }

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
