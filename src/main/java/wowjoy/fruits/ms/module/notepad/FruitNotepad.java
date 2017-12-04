package wowjoy.fruits.ms.module.notepad;

import wowjoy.fruits.ms.module.AbstractEntity;

import java.util.Date;

public class FruitNotepad extends AbstractEntity {
    private String content;

    private String userId;

    private String state;

    private Date notepadDate;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getNotepadDate() {
        return notepadDate;
    }

    public void setNotepadDate(Date notepadDate) {
        this.notepadDate = notepadDate;
    }
}