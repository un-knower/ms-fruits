package wowjoy.fruits.ms.module.notepad;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by wangziwen on 2017/12/8.
 */
public class FruitNotepadVo extends FruitNotepad {
    private String uuidVo;
    private Date startDate;
    private Date endDate;

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getUuidVo() {
        return uuidVo;
    }

    public void setUuidVo(String uuidVo) {
        this.uuidVo = uuidVo;
    }

    public LocalDateTime getStartLocalDateTime() {
        if (startDate == null) return null;
        return LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public LocalDateTime getEndLocalDateTime() {
        if (endDate == null) return null;
        return LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
    }

}
