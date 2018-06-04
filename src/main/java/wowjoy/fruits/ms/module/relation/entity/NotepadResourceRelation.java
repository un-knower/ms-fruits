package wowjoy.fruits.ms.module.relation.entity;

import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

public class NotepadResourceRelation extends AbstractEntity {
    private String notepadId;

    private String resourceId;

    private FruitDict.Resource nrType;

    public static class Update extends NotepadResourceRelation {
        public Update() {
            setUuid(null);
            setIsDeleted(null);
        }
    }

    public String getNotepadId() {
        return notepadId;
    }

    public void setNotepadId(String notepadId) {
        this.notepadId = notepadId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public FruitDict.Resource getNrType() {
        return nrType;
    }

    public void setNrType(FruitDict.Resource nrType) {
        this.nrType = nrType;
    }
}