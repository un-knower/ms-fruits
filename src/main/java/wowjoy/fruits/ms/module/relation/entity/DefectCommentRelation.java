package wowjoy.fruits.ms.module.relation.entity;

import wowjoy.fruits.ms.module.AbstractEntity;

public class DefectCommentRelation extends AbstractEntity {
    private String commentId;

    private String defectId;

    public static class Update extends DefectCommentRelation {
        public Update() {
            setUuid(null);
            setIsDeleted(null);
        }
    }

    public String getCommentId() {
        return commentId;
    }

    public String getDefectId() {
        return defectId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public void setDefectId(String defectId) {
        this.defectId = defectId;
    }
}