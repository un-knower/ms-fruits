package wowjoy.fruits.ms.module.notepad;

import com.google.common.collect.Lists;
import wowjoy.fruits.ms.module.logs.FruitLogsDao;
import wowjoy.fruits.ms.module.user.FruitUserDao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Created by wangziwen on 2017/12/8.
 */
public class FruitNotepadDao extends FruitNotepad {
    public FruitNotepadDao() {
        setUuid(null);
    }

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private FruitUserDao user;
    private List<FruitLogsDao> logs;

    public List<FruitLogsDao> getLogs() {
        return logs;
    }

    public void setLogs(List<FruitLogsDao> logs) {
        this.logs = logs;
    }

    public FruitUserDao getUser() {
        return user;
    }

    public void setUser(FruitUserDao user) {
        this.user = user;
    }

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
