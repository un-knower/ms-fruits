package wowjoy.fruits.ms.module.comment;

import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.EntityUtils;
import wowjoy.fruits.ms.module.user.FruitUser;

import java.util.ArrayList;

public class FruitComment extends AbstractEntity {

    private String comment;
    private String parentId;
    private String userId;

    public static class Insert extends FruitComment implements EntityUtils {
        public Insert() {
            setUuid(obtainUUID());
        }
    }

    public static class Info extends FruitComment {
        private FruitUser user;
        private ArrayList<? extends Info> comments;

        public ArrayList<? extends Info> getComments() {
            return comments;
        }

        public void setComments(ArrayList<? extends Info> comments) {
            this.comments = comments;
        }

        public FruitUser getUser() {
            return user;
        }

        public void setUser(FruitUser user) {
            this.user = user;
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}