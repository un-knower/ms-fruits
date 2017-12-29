package wowjoy.fruits.ms.module.notepad;

import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class FruitNotepad extends AbstractEntity {
    private String content;

    private String userId;

    private String state;

    /*预计提交时间*/
    private Date estimatedSubmitDate;

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

    public Date getEstimatedSubmitDate() {
        return estimatedSubmitDate;
    }
//
//    public void setEstimatedSubmitDate(Date estimatedSubmitDate) {
//        if (estimatedSubmitDate == null)
//            throw new CheckException("必须提供日报预计结束时间");
//        this.estimatedSubmitDate = Date.from(LocalDateTime.ofInstant(estimatedSubmitDate.toInstant(), ZoneId.systemDefault()).toLocalDate().atTime(0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
//        selectState();
//    }

    private void selectState() {
        LocalDateTime notepadDateTime = LocalDateTime.ofInstant(estimatedSubmitDate.toInstant(), ZoneId.systemDefault());
        notepadDateTime = LocalDateTime.of(notepadDateTime.getYear(),
                notepadDateTime.getMonth(),
                notepadDateTime.getDayOfMonth(),
                23,
                59,
                59);
        LocalDateTime now = LocalDateTime.now();
        if (Duration.between(now, notepadDateTime).toHours() < 0)
            this.setState(FruitDict.NotepadDict.PAY_SUBMIT.name());
        else
            this.setState(FruitDict.NotepadDict.PUNCTUAL_SUBMIT.name());
    }

    public void setNotepadDate(LocalDateTime notePadDate) {
        this.estimatedSubmitDate = Date.from(notePadDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static FruitNotepadDao getDao() {
        return new FruitNotepadDao();
    }

    public static FruitNotepadVo getVo() {
        return new FruitNotepadVo();
    }

    public static FruitNotepadEmpty getEmpty() {
        return new FruitNotepadEmpty();
    }
}