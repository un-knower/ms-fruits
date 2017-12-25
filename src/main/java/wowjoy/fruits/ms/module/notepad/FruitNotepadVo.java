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

    public String getUuidVo() {
        return uuidVo;
    }

    public void setUuidVo(String uuidVo) {
        this.uuidVo = uuidVo;
    }

    public LocalDateTime getStartDate() {
        return LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
    }

    public LocalDateTime getEndDate() {
        return LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
    }

}
