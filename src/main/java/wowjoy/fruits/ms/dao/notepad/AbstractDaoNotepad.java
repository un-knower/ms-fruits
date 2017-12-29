package wowjoy.fruits.ms.dao.notepad;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.dao.logs.LogsTemplate;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.logs.FruitLogsDao;
import wowjoy.fruits.ms.module.notepad.FruitNotepad;
import wowjoy.fruits.ms.module.notepad.FruitNotepadDao;
import wowjoy.fruits.ms.module.notepad.FruitNotepadVo;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

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

    protected abstract List<FruitNotepadDao> findsByCurrentIds(FruitNotepadDao dao);

    protected abstract List<FruitNotepadDao> findsByTeamIds(FruitNotepadDao dao, String... teamIds);

    protected abstract List<FruitNotepadDao> joinLogs(LinkedList<String> ids);

    protected abstract List<FruitNotepadDao> joinUser(LinkedList<String> ids);

    public final void insert(FruitNotepadVo vo) {
        FruitNotepadDao dao = FruitNotepad.getDao();
        dao.setUuid(vo.getUuid());
        dao.setContent(vo.getContent());
//        dao.setEstimatedSubmitDate(vo.getEstimatedSubmitDate());
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

    public final List<FruitNotepadDao> findByCurrentUser(FruitNotepadVo vo) {
        FruitNotepadDao dao = FruitNotepad.getDao();
        dao.setStartDate(vo.getStartDate());
        dao.setEndDate(vo.getEndDate());
        dao.setUserId(ApplicationContextUtils.getCurrentUser().getUserId());
        dao.setState(vo.getState());
        List<FruitNotepadDao> result = findsByCurrentIds(dao);
        if (result.isEmpty())
            return result;
        LinkedList<String> ids = toIds(result);
        DaoThread thread = DaoThread.getFixed();
        thread.execute(this.plugLogs(ids, result)).execute(this.plugUser(ids, result)).get();
        thread.shutdown();
        return result;
    }


    public final List<FruitNotepadDao> findTeam(FruitNotepadVo vo, String teamId) {
        FruitNotepadDao dao = FruitNotepad.getDao();
        dao.setStartDate(vo.getStartDate());
        dao.setEndDate(vo.getEndDate());
        dao.setState(vo.getState());
        List<FruitNotepadDao> result = findsByTeamIds(dao, teamId);
        if (result.isEmpty())
            return result;
        LinkedList<String> ids = toIds(result);
        DaoThread thread = DaoThread.getFixed();
        thread.execute(this.plugLogs(ids, result)).execute(this.plugUser(ids, result)).get();
        thread.shutdown();
        return result;
    }

    private LinkedList<String> toIds(List<FruitNotepadDao> result) {
        LinkedList ids = new LinkedList();
        result.forEach((notepad) -> ids.add(notepad.getUuid()));
        return ids;
    }

    private Callable plugLogs(LinkedList<String> ids, List<FruitNotepadDao> notepads) {
        return () -> {
            LogsTemplate logsTemplate = LogsTemplate.newInstance(FruitDict.Parents.NOTEPAD);
            LinkedHashMap<String, List<FruitLogsDao>> keyValue = Maps.newLinkedHashMap();
            this.joinLogs(ids).forEach((notepad) -> {
                notepad.getLogs().forEach(logs -> logs.setMsg(logsTemplate.msg(logs)));
                keyValue.put(notepad.getUuid(), notepad.getLogs());
            });
            notepads.forEach((notepad) -> {
                if (keyValue.containsKey(notepad.getUuid()))
                    notepad.setLogs(keyValue.get(notepad.getUuid()));
                else
                    notepad.setLogs(Lists.newLinkedList());
            });
            return true;
        };
    }

    private Callable plugUser(LinkedList<String> ids, List<FruitNotepadDao> notepads) {
        return () -> {
            LinkedHashMap<String, FruitUserDao> keyValue = Maps.newLinkedHashMap();
            this.joinUser(ids).forEach((notepad) -> keyValue.put(notepad.getUuid(), notepad.getUser()));
            notepads.forEach((notepad) -> {
                if (keyValue.containsKey(notepad.getUuid()))
                    notepad.setUser(keyValue.get(notepad.getUuid()));
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
