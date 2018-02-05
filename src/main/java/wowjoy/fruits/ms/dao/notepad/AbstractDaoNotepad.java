package wowjoy.fruits.ms.dao.notepad;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.logs.FruitLogsDao;
import wowjoy.fruits.ms.module.notepad.FruitNotepad;
import wowjoy.fruits.ms.module.notepad.FruitNotepadDao;
import wowjoy.fruits.ms.module.notepad.FruitNotepadExample;
import wowjoy.fruits.ms.module.notepad.FruitNotepadVo;
import wowjoy.fruits.ms.module.team.FruitTeamDao;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static java.util.stream.Collectors.*;

/**
 * Created by wangziwen on 2017/8/25.
 */
public abstract class AbstractDaoNotepad implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/
    protected abstract FruitNotepad find(FruitNotepadDao dao);

    protected abstract void insert(FruitNotepadDao dao);

    protected abstract void update(FruitNotepadDao dao);

    protected abstract void delete(FruitNotepadDao dao);

    protected abstract List<FruitNotepadDao> findsByExample(Consumer<FruitNotepadExample> exampleConsumer);

    protected abstract Map<String, LinkedList<FruitLogsDao>> joinLogs(LinkedList<String> ids);

    protected abstract List<FruitUserDao> joinUser(LinkedList<String> ids);

    public abstract FruitTeamDao findTeamInfo(String teamId);

    public final void insert(FruitNotepadVo vo) {
        FruitNotepadDao dao = FruitNotepad.getDao();
        dao.setUuid(vo.getUuid());
        dao.setContent(vo.getContent());
        dao.setEstimatedSubmitDateAndState(vo.getEstimatedSubmitDate());
        dao.setUserId(ApplicationContextUtils.getCurrentUser().getUserId());
        this.insertCheckNotepad(dao);
        this.insert(dao);
    }

    public final void update(FruitNotepadVo vo) {
        FruitNotepadDao dao = FruitNotepad.getDao();
        dao.setUuid(vo.getUuidVo());
        dao.setContent(vo.getContent());
        this.update(dao);
    }

    public final void delete(String uuid) {
        FruitNotepadDao dao = FruitNotepad.getDao();
        dao.setUuid(uuid);
        this.delete(dao);
    }

    public final List<FruitNotepadDao> findNotepadByCurrentUserId(FruitNotepadVo vo) {
        FruitUser currentUser = ApplicationContextUtils.getCurrentUser();
        List<FruitNotepadDao> notepadDaoList = findsByExample(example -> {
            FruitNotepadExample.Criteria criteria = example.createCriteria();
            if (vo.getStartDate() != null && vo.getEndDate() != null)
                criteria.andEstimatedSubmitDateBetween(vo.getStartDate(), vo.getEndDate());
            if (StringUtils.isNotBlank(vo.getState()))
                criteria.andStateEqualTo(vo.getState());
            criteria.andUserIdEqualTo(currentUser.getUserId());
            criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name());
            example.setOrderByClause("estimated_submit_date desc,create_date_time desc");
        });
        if (notepadDaoList.isEmpty())
            return notepadDaoList;
        DaoThread.getFixed().execute(this.plugLogs(notepadDaoList)).execute(this.plugUser(notepadDaoList)).get().shutdown();
        return notepadDaoList;
    }

    public final FruitTeamDao findNotepadByTeamId(FruitNotepadVo vo, String teamId) {
        if (StringUtils.isBlank(teamId)) throw new CheckException("团队id不能为空");
        FruitTeamDao teamInfo = this.findTeamInfo(teamId);
        List<FruitNotepadDao> notepadDaoList = this.findsByExample(example -> {
            FruitNotepadExample.Criteria criteria = example.createCriteria();
            if (vo.getStartDate() != null && vo.getEndDate() != null)
                criteria.andEstimatedSubmitDateBetween(vo.getStartDate(), vo.getEndDate());
            if (StringUtils.isNotBlank(vo.getState()))
                criteria.andStateEqualTo(vo.getState());
            criteria.andUserIdIn(teamInfo.getUsers().parallelStream().map(FruitUserDao::getUserId).collect(toList()));
            criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name());
            example.setOrderByClause("estimated_submit_date desc,create_date_time desc");
        });
        DaoThread notepadThread = DaoThread.getFixed().execute(this.plugLogs(notepadDaoList)).execute(this.plugUser(notepadDaoList));
        Map<String, List<FruitNotepadDao>> notepadMap = notepadDaoList.stream().collect(groupingBy(FruitNotepadDao::getUserId));
        teamInfo.getUsers().parallelStream().forEach(user -> user.setNotepadDaos(notepadMap.get(user.getUserId())));
        notepadThread.get().shutdown();
        return teamInfo;
    }

    public final Optional<FruitTeamDao> findNotepadMonthByTeamId(LocalDate date, String teamId) {
        if (date == null) return Optional.empty();
        LocalDate startDate = LocalDate.of(date.getYear(), date.getMonth(), 1);
        LocalDate endDate = LocalDate.of(date.getYear(), date.getMonth(), date.lengthOfMonth());
        FruitNotepadVo vo = FruitNotepad.getVo();
        vo.setStartDate(Date.from(startDate.atTime(0, 0, 0).atZone(ZoneId.systemDefault()).toInstant()));
        vo.setEndDate(Date.from(endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()));
        return Optional.of(this.findNotepadByTeamId(vo, teamId));
    }

    private Callable plugLogs(List<FruitNotepadDao> notepads) {
        return () -> {
            if (notepads == null || notepads.isEmpty()) return false;
            Map<String, LinkedList<FruitLogsDao>> logs = this.joinLogs(notepads.parallelStream().map(FruitNotepadDao::getUuid).collect(toCollection(LinkedList::new)));
            notepads.parallelStream().forEach(notepad -> notepad.setLogs(logs.get(notepad.getUuid())));
            return true;
        };
    }

    private Callable plugUser(List<FruitNotepadDao> notepads) {
        return () -> {
            if (notepads == null || notepads.isEmpty()) return false;
            Map<String, List<FruitUserDao>> userMap = this.joinUser(notepads.parallelStream().map(FruitNotepadDao::getUserId).collect(toCollection(LinkedList::new)))
                    .parallelStream().collect(groupingBy(FruitUserDao::getUserId));
            notepads.parallelStream().forEach(notepad -> {
                if (userMap.containsKey(notepad.getUserId()))
                    notepad.setUser(userMap.get(notepad.getUserId()).stream().findAny().get());
            });
            return true;
        };
    }


    /*检查添加参数*/
    private void insertCheckNotepad(FruitNotepadDao dao) {
        if (StringUtils.isBlank(dao.getUserId()))
            throw new CheckException("未检测到关联用户");
    }

}
