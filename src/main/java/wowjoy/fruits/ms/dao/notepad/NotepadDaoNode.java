package wowjoy.fruits.ms.dao.notepad;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.AbstractDaoChain;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.notepad.FruitNotepad;
import wowjoy.fruits.ms.module.notepad.FruitNotepadDao;
import wowjoy.fruits.ms.module.plan.FruitPlan;
import wowjoy.fruits.ms.module.plan.FruitPlanDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

/**
 * Created by wangziwen on 2017/11/27.
 */
public class NotepadDaoNode extends AbstractDaoChain {
    private AbstractDaoNotepad planDao = ApplicationContextUtils.getContext().getBean(NotepadDaoImpl.class);

    public NotepadDaoNode(FruitDict.Parents type) {
        super(type);
    }

    @Override
    public AbstractEntity find(String uuid) {
        if (!super.type.name().equals(FruitDict.Parents.PLAN.name()))
            return super.getNext().find(uuid);
        if (StringUtils.isBlank(uuid))
            return null;
        FruitNotepadDao dao = FruitNotepad.getDao();
        dao.setUuid(uuid);
        FruitNotepad data = planDao.find(dao);
        if (!data.isNotEmpty()) return null;
        return data;
    }
}
