package wowjoy.fruits.ms.dao.project;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.dao.relation.AbstractDaoRelation;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.project.FruitProjectDao;
import wowjoy.fruits.ms.module.project.FruitProjectVo;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

/**
 * Created by wangziwen on 2017/9/6.
 */
public abstract class AbstractDaoProject implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    /**
     * 1、添加项目信息
     * 2、添加团队关联
     * 3、添加用户关联
     */
    protected abstract void insert(FruitProjectDao dao);

    /**
     * 查询项目信息，支持查询条件：
     * 1、根据标题查询
     * 2、根据项目状态查询
     */
    protected abstract List<FruitProject> finds(FruitProjectDao dao);

    protected abstract void update(FruitProjectDao dao);

    protected abstract void updateStatus(FruitProjectDao dao);

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public void insert(FruitProjectVo vo) {
        final FruitProjectDao dao = FruitProject.getProjectDao();
        dao.setUuid(vo.getUuid());
        dao.setTitle(vo.getTitle());
        dao.setProjectStatus(vo.getProjectStatus());
        dao.setPredictEndDate(vo.getPredictEndDate());
        dao.setDescription(vo.getDescription());
        dao.setTeamDao(vo.getTeamVo());
        dao.setUserDao(vo.getUserVo());
        dao.setProjectStatus(FruitDict.ProjectDict.UNDERWAY.name());
        this.insert(dao);
    }

    public List<FruitProject> finds(FruitProjectVo vo) {
        final FruitProjectDao dao = FruitProject.getProjectDao();
        dao.setUuid(vo.getUuidVo());
        dao.setTitle(vo.getTitle());
        dao.setProjectStatus(vo.getProjectStatus());
        return this.finds(dao);
    }

    public FruitProject findByUUID(String uuid) {
        final FruitProjectDao dao = FruitProject.getProjectDao();
        dao.setUuid(uuid);
        final List data = this.finds(dao);
        if (data.isEmpty())
            throw new CheckProjectException("【findByUUID】无匹配信息");
        return (FruitProject) data.get(0);
    }

    public void update(FruitProjectVo vo) {
        final FruitProjectDao dao = FruitProject.getProjectDao();
        dao.setUuid(this.findByUUID(vo.getUuidVo()).getUuid());
        dao.setTitle(vo.getTitle());
        dao.setDescription(vo.getDescription());
        dao.setPredictEndDate(vo.getPredictEndDate());
        dao.setProjectStatus(vo.getProjectStatus());
        dao.setTeamDao(vo.getTeamVo());
        dao.setUserDao(vo.getUserVo());
        this.update(dao);
    }

    public void updateStatus(FruitProjectVo vo) {
        final FruitProjectDao data = FruitProject.getProjectDao();
        data.setUuid(this.findByUUID(vo.getUuidVo()).getUuid());
        data.setProjectStatus(vo.getProjectStatus());
        this.updateStatus(data);
    }

    /*******************************************************
     * 仅用于实现类。内部类尽量采用static，非静态类不利于垃圾回收。 *
     *******************************************************/
    protected static class CheckProjectException extends CheckException {
        public CheckProjectException(String message) {
            super("【Project exception】" + message);
        }
    }

}
