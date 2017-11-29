package wowjoy.fruits.ms.module.relation.entity;

import wowjoy.fruits.ms.module.AbstractEntity;

public class TaskListRelation extends AbstractEntity {
    private String taskId;
    private String listId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public static TaskListRelation newInstance(String taskId, String listId) {
        TaskListRelation result = new TaskListRelation();
        result.setTaskId(taskId);
        result.setListId(listId);
        return result;
    }

    public static TaskListRelationEmpty getEmpty() {
        return new TaskListRelationEmpty();
    }

    public static TaskListRelation getInstance() {
        return new TaskListRelation();
    }

    public static class TaskListRelationEmpty extends TaskListRelation {
        @Override
        public boolean isNotEmpty() {
            return false;
        }
    }
}