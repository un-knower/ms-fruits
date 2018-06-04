package wowjoy.fruits.ms.module.task;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.entity.*;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.*;


/**
 * Created by wangziwen on 2017/10/9.
 */
public class FruitTaskVo extends FruitTask {
    private String uuidVo;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private Map<FruitDict.Systems, List<TaskPlanRelation>> planRelation;
    private Map<FruitDict.Systems, List<TaskProjectRelation>> projectRelation;
    private Map<FruitDict.Systems, List<TaskUserRelation>> userRelation;
    private Map<FruitDict.Systems, List<TaskListRelation>> listRelation;
    private String projectId;
    private String listTitle;

    public String getListTitle() {
        return listTitle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
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

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    private List<String> split(String ids) {
        try {
            return Arrays.asList(ids.split(","));
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public String getUuidVo() {
        return uuidVo;
    }

    public void setUuidVo(String uuidVo) {
        this.uuidVo = uuidVo;
    }

    public Map<FruitDict.Systems, List<TaskPlanRelation>> getPlanRelation() {
        return planRelation;
    }

    public void setPlanRelation(Map<FruitDict.Systems, List<TaskPlanRelation>> planRelation) {
        this.planRelation = planRelation;
    }

    public Map<FruitDict.Systems, List<TaskProjectRelation>> getProjectRelation() {
        return projectRelation;
    }

    public List<TaskProjectRelation> getProjectRelation(FruitDict.Systems parents) {
        return projectRelation != null && projectRelation.containsKey(parents) ? projectRelation.get(parents) : Lists.newLinkedList();
    }

    public List<TaskPlanRelation> getPlanRelation(FruitDict.Systems parents) {
        return planRelation != null && planRelation.containsKey(parents) ? planRelation.get(parents) : Lists.newLinkedList();
    }

    public void setProjectRelation(Map<FruitDict.Systems, List<TaskProjectRelation>> projectRelation) {
        this.projectRelation = projectRelation;
    }

    public Map<FruitDict.Systems, List<TaskUserRelation>> getUserRelation() {
        return userRelation;
    }

    public Optional<List<TaskUserRelation>> getUserRelation(FruitDict.Systems parent) {
        return userRelation != null && userRelation.containsKey(parent) ? Optional.of(userRelation.get(parent)) : Optional.empty();
    }

    public void setUserRelation(Map<FruitDict.Systems, List<TaskUserRelation>> userRelation) {
        this.userRelation = userRelation;
    }

    public Map<FruitDict.Systems, List<TaskListRelation>> getListRelation() {
        return listRelation;
    }

    public void setListRelation(Map<FruitDict.Systems, List<TaskListRelation>> listRelation) {
        this.listRelation = listRelation;
    }

    public static class TaskTransferVo extends FruitTaskVo {
        private String reason;

        private LinkedList<TransferUserRelation> transferUser;

        private String transferId;

        public String getTransferId() {
            return transferId;
        }

        public void setTransferId(String transferId) {
            this.transferId = transferId;
        }

        public LinkedList<TransferUserRelation> getTransferUser() {
            return transferUser;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

}
