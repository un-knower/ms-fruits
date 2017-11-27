package wowjoy.fruits.ms.dao.list;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.AbstractDaoChain;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.list.FruitList;
import wowjoy.fruits.ms.module.list.FruitListDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

/**
 * Created by wangziwen on 2017/11/27.
 */
public class ListDaoNode extends AbstractDaoChain {
    private AbstractDaoList listDao = ApplicationContextUtils.getContext().getBean(ListDaoImpl.class);

    public ListDaoNode(FruitDict.Parents type) {
        super(type);
    }

    @Override
    public AbstractEntity find(String uuid) {
        if (!super.type.name().equals(FruitDict.Parents.List.name()))
            super.getNext().find(uuid);
        if (StringUtils.isBlank(uuid))
            return null;
        FruitListDao dao = FruitList.getDao();
        dao.setUuid(uuid);
        FruitList data = listDao.find(dao);
        if (!data.isNotEmpty()) return null;
        return data;
    }
}
