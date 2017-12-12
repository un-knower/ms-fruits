package wowjoy.fruits.ms.module.notepad;

import java.time.LocalDateTime;

/**
 * Created by wangziwen on 2017/12/8.
 */
public class FruitNotepadVo extends FruitNotepad {
    private String uuidVo;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public String getUuidVo() {
        return uuidVo;
    }

    public void setUuidVo(String uuidVo) {
        this.uuidVo = uuidVo;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
