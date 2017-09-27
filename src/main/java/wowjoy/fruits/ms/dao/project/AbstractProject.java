package wowjoy.fruits.ms.dao.project;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.project.FruitProjectDao;
import wowjoy.fruits.ms.module.project.FruitProjectVo;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.List;

/**
 * Created by wangziwen on 2017/9/6.
 */
public abstract class AbstractProject implements InterfaceDao {
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
    protected abstract List<FruitProjectDao> findRelation(FruitProjectDao dao);

    protected abstract List<FruitProjectDao> finds(FruitProjectDao dao);

    protected abstract FruitProject findByUUID(String uuid);

    protected abstract void update(FruitProjectDao dao);

    protected abstract void updateStatus(FruitProjectDao dao);

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public final void insert(FruitProjectVo vo) {
        final FruitProjectDao dao = FruitProject.getProjectDao();
        dao.setUuid(vo.getUuid());
        dao.setTitle(vo.getTitle());
        dao.setProjectStatus(vo.getProjectStatus());
        dao.setPredictEndDate(vo.getPredictEndDate());
        dao.setDescription(vo.getDescription());
        dao.setTeamRelation(vo.getTeamVo());
        dao.setUserRelation(vo.getUserVo());
        dao.setProjectStatus(FruitDict.ProjectDict.UNDERWAY.name());
        this.insert(dao);
    }

    public final List<FruitProjectDao> findRelation(FruitProjectVo vo) {
        final FruitProjectDao dao = FruitProject.getProjectDao();
        dao.setUuid(vo.getUuidVo());
        dao.setTitle(vo.getTitle());
        dao.setProjectStatus(vo.getProjectStatus());
        List<FruitProjectDao> result = this.findRelation(dao);
        result.forEach((p) -> {
            p.computeDays();
            p.seekPrincipal();
        });
        return result;
    }

    public final List<FruitProjectDao> finds(FruitProjectVo vo) {
        final FruitProjectDao dao = FruitProject.getProjectDao();
        dao.setUuid(vo.getUuidVo());
        dao.setTitle(vo.getTitle());
        dao.setProjectStatus(vo.getProjectStatus());
        return this.finds(dao);
    }

    public final FruitProject findByUUID(FruitProjectVo vo) {
        return this.findByUUID(vo.getUuidVo());
    }

    public final void update(FruitProjectVo vo) {
        if (!this.findByUUID(vo.getUuidVo()).isNotEmpty())
            throw new CheckProjectException("项目不存在");
        final FruitProjectDao dao = FruitProject.getProjectDao();
        dao.setUuid(vo.getUuidVo());
        dao.setTitle(vo.getTitle());
        dao.setDescription(vo.getDescription());
        dao.setPredictEndDate(vo.getPredictEndDate());
        dao.setProjectStatus(vo.getProjectStatus());
        dao.setTeamRelation(vo.getTeamVo());
        dao.setUserRelation(vo.getUserVo());
        this.update(dao);
    }

    public final void updateStatus(FruitProjectVo vo) {
        if (!this.findByUUID(vo.getUuidVo()).isNotEmpty())
            throw new CheckProjectException("项目不存在");
        final FruitProjectDao data = FruitProject.getProjectDao();
        data.setUuid(vo.getUuidVo());
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
