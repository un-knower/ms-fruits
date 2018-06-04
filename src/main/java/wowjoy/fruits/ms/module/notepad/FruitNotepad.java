package wowjoy.fruits.ms.module.notepad;

import org.assertj.core.util.Lists;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.EntityUtils;
import wowjoy.fruits.ms.module.resource.FruitResource;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class FruitNotepad extends AbstractEntity {
    private String content;

    private String userId;

    private String state;

    /*预计提交时间*/
    private Date estimatedSubmitDate;

    public static class Insert extends FruitNotepad implements EntityUtils {
        public Insert() {
            setUuid(obtainUUID());
            setIsDeleted(FruitDict.Systems.N.name());
        }

        private ArrayList<Upload> uploads;

        public ArrayList<Upload> getUploads() {
            return uploads;
        }

        public void setUpload(Upload upload) {
            if (this.uploads == null)
                this.uploads = Lists.newArrayList();
            this.uploads.add(upload);
        }
    }

    public static class Update extends FruitNotepad {
        public Update() {
            setUuid(null);
            setIsDeleted(null);
        }

        private ArrayList<Upload> uploads;
        private String removeResource;

        public ArrayList<String> getRemoveResources() {
            return Lists.newArrayList(removeResource.split(","));
        }

        public String getRemoveResource() {
            return removeResource;
        }

        public void setRemoveResource(String removeResource) {
            this.removeResource = removeResource;
        }

        public ArrayList<Upload> getUploads() {
            return uploads;
        }

        public void setUploads(ArrayList<Upload> uploads) {
            this.uploads = uploads;
        }

        public void setUpload(Upload upload) {
            if (this.uploads == null)
                this.uploads = Lists.newArrayList();
            this.uploads.add(upload);
        }
    }

    public static class Upload extends FruitResource.Upload {
        public Upload() {
        }

        private FruitDict.Resource nrType;

        public FruitDict.Resource getNrType() {
            return nrType;
        }

        public void setNrType(FruitDict.Resource nrType) {
            this.nrType = nrType;
        }
    }

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

    public void setEstimatedSubmitDate(Date estimatedSubmitDate) {
        this.estimatedSubmitDate = estimatedSubmitDate;
    }

    /*计算日报状态*/
    public void selectState() {
        LocalDateTime notepadDateTime = LocalDateTime.ofInstant(estimatedSubmitDate.toInstant(), ZoneId.systemDefault());
        notepadDateTime = LocalDateTime.of(notepadDateTime.getYear(),
                notepadDateTime.getMonth(),
                notepadDateTime.getDayOfMonth(),
                23,
                59,
                59);
        LocalDateTime now = LocalDateTime.ofInstant(this.getCreateDateTime().toInstant(), ZoneId.systemDefault());
        if (Duration.between(now, notepadDateTime).toHours() <= -12)
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