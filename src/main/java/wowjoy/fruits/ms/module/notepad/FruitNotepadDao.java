package wowjoy.fruits.ms.module.notepad;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by wangziwen on 2017/12/8.
 */
public class FruitNotepadDao extends FruitNotepad {
    public FruitNotepadDao() {
        setUuid(null);
    }

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Date getStartDate() {
        return startDate != null ? Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate != null ? Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
