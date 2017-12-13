package wowjoy.fruits.ms.dao.notepad;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.notepad.FruitNotepad;
import wowjoy.fruits.ms.module.notepad.FruitNotepadDao;
import wowjoy.fruits.ms.module.notepad.FruitNotepadVo;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.util.List;

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

    protected abstract List<FruitNotepadDao> finds(FruitNotepadDao dao);

    protected abstract List<FruitNotepadDao> finds(FruitNotepadDao dao, String... teamIds);

    public final void insert(FruitNotepadVo vo) {
        FruitNotepadDao dao = FruitNotepad.getDao();
        dao.setUuid(vo.getUuid());
        dao.setContent(vo.getContent());
        dao.setEstimatedSubmitDate(vo.getEstimatedSubmitDate());
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
        return finds(dao);
    }

    public final List<FruitNotepadDao> findTeam(FruitNotepadVo vo, String teamId) {
        FruitNotepadDao dao = FruitNotepad.getDao();
        dao.setStartDate(vo.getStartDate());
        dao.setEndDate(vo.getEndDate());
        dao.setState(vo.getState());
        return finds(dao, teamId);
    }


    /*检查添加参数*/
    private void insertCheckNotepad(FruitNotepadDao dao) {
        if (StringUtils.isBlank(dao.getUserId()))
            throw new CheckException("未检测到关联用户");
    }

}
