package wowjoy.fruits.ms.module.relation.entity;

import wowjoy.fruits.ms.module.AbstractEntity;

public class ProjectListRelation extends AbstractEntity {

    private String projectId;

    private String listId;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public static ProjectListRelation newInstance(String projectId, String listId) {
        ProjectListRelation result = new ProjectListRelation();
        result.setProjectId(projectId);
        result.setListId(listId);
        return result;
    }

    public static ProjectListRelation getInstance() {
        return new ProjectListRelation();
    }
}