package wowjoy.fruits.ms.dao.project;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.AbstractDaoChain;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

/**
 * Created by wangziwen on 2017/11/27.
 */
public class ProjectDaoNode extends AbstractDaoChain {
    private AbstractDaoProject projectDao = ApplicationContextUtils.getContext().getBean(ProjectDaoImpl.class);

    public ProjectDaoNode(FruitDict.Parents type) {
        super(type);
    }

    @Override
    public AbstractEntity find(String uuid) {
        if (!super.type.name().equals(FruitDict.Parents.PROJECT.name()))
            return super.getNext().find(uuid);
        if (StringUtils.isBlank(uuid))
            return null;
        return projectDao.find(example -> example.createCriteria().andUuidEqualTo(uuid)).orElse(null);
    }
}
