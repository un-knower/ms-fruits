package wowjoy.fruits.ms.module.versions;

import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.EntityUtils;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.user.FruitUser;

import java.util.ArrayList;

public class FruitVersions extends AbstractEntity {
    private String versions;

    private String parentId;

    private String userId;

    private String projectId;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getVersions() {
        return versions;
    }

    public void setVersions(String versions) {
        this.versions = versions;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static class Insert extends FruitVersions implements EntityUtils {
        public Insert() {
            this.setUuid(obtainUUID());
        }
    }

    public static class Update extends FruitVersions {
        public Update() {
            this.setUuid(null);
        }
    }

    public static class Search extends FruitVersions {
        public Search() {
            setUuid(null);
            setIsDeleted(null);
        }

        /*分页参数*/
        private int pageNum;    //页码
        private int pageSize;   //行数

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
    }

    public static class Info extends FruitVersions {
        public Info() {
            setUuid(null);
            setIsDeleted(null);
        }

        private FruitUser user;
        private FruitProject project;
        private ArrayList<FruitVersions.Info> sons;
        private boolean isUse;

        public boolean isUse() {
            return isUse;
        }

        public void setUse(boolean use) {
            isUse = use;
        }

        public FruitUser getUser() {
            return user;
        }

        public void setUser(FruitUser user) {
            this.user = user;
        }

        public FruitProject getProject() {
            return project;
        }

        public void setProject(FruitProject project) {
            this.project = project;
        }

        public ArrayList<Info> getSons() {
            return sons;
        }

        public void setSons(ArrayList<Info> sons) {
            this.sons = sons;
        }
    }
}