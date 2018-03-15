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
import wowjoy.fruits.ms.module.team.FruitTeamUser;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.*;
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
    protected abstract List<FruitNotepadDao> finds(Consumer<FruitNotepadExample> exampleConsumer);

    protected abstract void insert(FruitNotepadDao dao);

    protected abstract void update(FruitNotepadDao dao);

    protected abstract void delete(FruitNotepadDao dao);

    protected abstract List<FruitNotepadDao> findsByExampleAndCustom(Consumer<FruitNotepadExample> exampleConsumer, Consumer<List<String>> customConsumer);

    protected abstract Map<String, LinkedList<FruitLogsDao>> joinLogs(LinkedList<String> ids);

    protected abstract List<FruitUserDao> joinUser(LinkedList<String> ids);

    public abstract FruitTeamDao findTeamInfo(String teamId, Consumer<FruitUserExample> userExampleConsumer);

    public final void insert(FruitNotepadVo vo) {
        if (StringUtils.isBlank(vo.getContent()))
            throw new CheckException("内容不能为空");
        if (vo.getEstimatedSubmitDate() == null)
            throw new CheckException("日报预计提交日期不能为空");
        FruitNotepadDao dao = FruitNotepad.getDao();
        dao.setUuid(vo.getUuid());
        dao.setContent(vo.getContent());
        dao.setEstimatedSubmitDate(vo.getEstimatedSubmitDate());
        dao.setUserId(ApplicationContextUtils.getCurrentUser().getUserId());
        this.insertCheckNotepad(dao);
        this.insert(dao);
    }

    public final void update(FruitNotepadVo vo) {
        if (StringUtils.isBlank(vo.getContent()))
            throw new CheckException("内容不能为空");
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
        List<FruitNotepadDao> notepadDaoList = findsByExampleAndCustom(example -> {
            FruitNotepadExample.Criteria criteria = example.createCriteria();
            if (vo.getStartDate() != null && vo.getEndDate() != null) {
                criteria.andEstimatedSubmitDateBetween(
                        ToDate.apply(ToLocalDate.apply(vo.getStartDate()).withHour(0).withMinute(0).withSecond(0)),
                        ToDate.apply(ToLocalDate.apply(vo.getEndDate()).withHour(23).withMinute(59).withSecond(59))
                );
            }
            criteria.andUserIdEqualTo(currentUser.getUserId());
            criteria.andIsDeletedEqualTo(Systems.N.name());
            example.setOrderByClause("estimated_submit_date desc,create_date_time desc");
        }, customs -> {
            if (StringUtils.isNotBlank(vo.getState())) {
                    /*创建时间小于第二天12点，正常提交*/
                if (FruitDict.NotepadDict.PUNCTUAL_SUBMIT.name().equals(vo.getState()))
                    customs.add("create_date_time < date_add(date_format(`estimated_submit_date`, '%Y-%m-%d 23:59:59'),interval 12 hour)");
                    /*创建时间大于第二天12点，补交*/
                if (FruitDict.NotepadDict.PAY_SUBMIT.name().equals(vo.getState()))
                    customs.add("create_date_time >= date_add(date_format(`estimated_submit_date`, '%Y-%m-%d 23:59:59'),interval 12 hour)");
            }
        });
        if (notepadDaoList.isEmpty())
            return notepadDaoList;
        DaoThread.getFixed()
                .execute(this.plugLogs(notepadDaoList))
                .execute(this.plugUser(notepadDaoList))
                .execute(this.plugUtils(notepadDaoList)).get().shutdown();
        return notepadDaoList;
    }

    public final FruitTeamDao findNotepadByTeamId(FruitNotepadVo vo, String teamId) {
        if (StringUtils.isBlank(teamId)) throw new CheckException("团队id不能为空");
        FruitTeamDao teamInfo = this.findTeamInfo(teamId, example -> {
            final FruitUserExample.Criteria criteria = example.createCriteria();
            Optional.ofNullable(vo.getUserName()).filter(StringUtils::isNotBlank).ifPresent(userName -> criteria.andUserNameLike(MessageFormat.format("%{0}%", userName)));
            criteria.andIsDeletedEqualTo(Systems.N.name());
        });
        /*未查询到用户信息直接退出函数*/
        if (teamInfo.findUsers().orElseGet(ArrayList::new).isEmpty())
            return teamInfo;
        /*查询指定用户的日报*/
        List<FruitNotepadDao> notepadDaoList = this.findsByExampleAndCustom(example -> {
            /*日报查询条件*/
            FruitNotepadExample.Criteria criteria = example.createCriteria();
            if (vo.getStartDate() != null && vo.getEndDate() != null)
                criteria.andEstimatedSubmitDateBetween(vo.getStartDate(), vo.getEndDate());
            criteria.andUserIdIn(teamInfo.findUsers().orElseGet(LinkedList::new).parallelStream().map(FruitTeamUser::getUserId).collect(toList()));
            criteria.andIsDeletedEqualTo(Systems.N.name());
            example.setOrderByClause("estimated_submit_date desc,create_date_time desc");
        }, customs -> {
            /*自定义日报查询范围*/
            if (StringUtils.isNotBlank(vo.getState())) {
                    /*创建时间小于第二天12点，正常提交*/
                if (FruitDict.NotepadDict.PUNCTUAL_SUBMIT.name().equals(vo.getState()))
                    customs.add("create_date_time < date_add(date_format(`estimated_submit_date`, '%Y-%m-%d 23:59:59'),interval 12 hour)");
                    /*创建时间大于第二天12点，补交*/
                if (FruitDict.NotepadDict.PAY_SUBMIT.name().equals(vo.getState()))
                    customs.add("create_date_time >= date_add(date_format(`estimated_submit_date`, '%Y-%m-%d 23:59:59'),interval 12 hour)");
            }
        });
        /*组合日报关联信息*/
        DaoThread notepadThread = DaoThread.getFixed()
                .execute(this.plugLogs(notepadDaoList))
                .execute(this.plugUser(notepadDaoList))
                .execute(this.plugUtils(notepadDaoList));
        /*融合日报、团队*/
        Map<String, List<FruitNotepadDao>> notepadMap = notepadDaoList.stream().collect(groupingBy(FruitNotepadDao::getUserId));
        if (StringUtils.isNotBlank(vo.getState()))
            teamInfo.setUsers(teamInfo.findUsers().orElseGet(LinkedList::new).parallelStream().filter(user -> {
                if (FruitDict.NotepadDict.NOT_SUBMIT.name().equals(vo.getState()))
                    return !notepadMap.containsKey(user.getUserId());
                else if (FruitDict.NotepadDict.PAY_SUBMIT.name().equals(vo.getState())
                        || FruitDict.NotepadDict.PUNCTUAL_SUBMIT.name().equals(vo.getState()))
                    return notepadMap.containsKey(user.getUserId());
                else
                    return true;
            }).collect(toList()));
        teamInfo.findUsers().orElseGet(LinkedList::new).parallelStream().forEach(user -> user.setMultiNotepad(notepadMap.get(user.getUserId())));
        notepadThread.get().shutdown();
        return teamInfo;
    }

    public final Optional<FruitTeamDao> findNotepadMonthByTeamId(LocalDate date, String teamId) {
        if (date == null) return Optional.empty();
        LocalDate startDate = LocalDate.of(date.getYear(), date.getMonth(), 1);
        LocalDate endDate = LocalDate.of(date.getYear(), date.getMonth(), date.lengthOfMonth());
        FruitNotepadVo vo = FruitNotepad.getVo();
        vo.setStartDate(ToDate.apply(startDate.atTime(0, 0, 0)));
        vo.setEndDate(ToDate.apply(endDate.atTime(23, 59, 59)));
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

    private Callable plugUtils(List<FruitNotepadDao> notepads) {
        return () -> {
            if (notepads == null || notepads.isEmpty()) return false;
            notepads.parallelStream().forEach(notepad -> notepad.selectState());
            return true;
        };
    }


    /*检查添加参数*/
    private void insertCheckNotepad(FruitNotepadDao dao) {
        if (StringUtils.isBlank(dao.getUserId()))
            throw new CheckException("未检测到关联用户");
    }

}
