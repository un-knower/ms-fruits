package wowjoy.fruits.ms.dao.project;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.project.FruitProjectDao;
import wowjoy.fruits.ms.module.project.FruitProjectVo;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.time.LocalDateTime;
import java.util.List;

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
    protected abstract List<FruitProjectDao> findRelation(FruitProjectDao dao);

    protected abstract List<FruitProjectDao> finds(FruitProjectDao dao);

    protected abstract FruitProjectDao findByUUID(String uuid);

    protected abstract void update(FruitProjectDao dao);

    protected abstract void delete(String uuid);

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public final void delete(FruitProjectVo vo) {
        if (!this.findByUUID(vo).isNotEmpty())
            throw new CheckProjectException("项目不存在");
        delete(vo.getUuidVo());
    }

    public final void insert(FruitProjectVo vo) {
        final FruitProjectDao dao = FruitProject.getProjectDao();
        dao.setUuid(vo.getUuid());
        dao.setTitle(vo.getTitle());
        dao.setProjectStatus(vo.getProjectStatus());
        dao.setPredictEndDate(vo.getPredictEndDate());
        dao.setDescription(vo.getDescription());
        dao.setTeamRelation(vo.getTeamRelation());
        dao.setUserRelation(vo.getUserRelation());
        dao.setProjectStatus(FruitDict.ProjectDict.UNDERWAY.name());

        if (dao.getTeamRelation(FruitDict.Dict.ADD).isEmpty() || dao.getUserRelation(FruitDict.Dict.ADD).isEmpty())
            throw new CheckException("必须绑定至少一个 团队 和 参与人");
        this.insert(dao);
    }

    public final List<FruitProjectDao> findRelation(FruitProjectVo vo) {
        final FruitProjectDao dao = FruitProject.getProjectDao();
        dao.setUuid(vo.getUuidVo());
        dao.setTitle(vo.getTitle());
        dao.setProjectStatus(vo.getProjectStatus());
        List<FruitProjectDao> result = this.findRelation(dao);
        result.forEach((project) -> {
            project.computeDays();
            project.seekPrincipal();
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

    public final FruitProjectDao findByUUID(FruitProjectVo vo) {
        FruitProjectDao result = this.findByUUID(vo.getUuidVo());
        result.computeDays().seekPrincipal();
        return result;
    }

    /**
     * 修改项目信息
     * 贴士：
     * 1、暂时不支持修改项目状态，需修改项目状态需要直接调用complete接口
     *
     * @param vo
     */
    public final void update(FruitProjectVo vo) {
        FruitProjectDao project = this.findByUUID(vo);
        if (!project.isNotEmpty())
            throw new CheckProjectException("项目不存在");
        final FruitProjectDao dao = FruitProject.getProjectDao();
        dao.setUuid(vo.getUuidVo());
        dao.setTitle(vo.getTitle());
        dao.setDescription(vo.getDescription());
        dao.setPredictEndDate(vo.getPredictEndDate());
        dao.setTeamRelation(vo.getTeamRelation());
        dao.setUserRelation(vo.getUserRelation());

        this.update(dao);
    }

    public final void complete(FruitProjectVo vo) {
        FruitProjectDao project = this.findByUUID(vo);
        if (!project.isNotEmpty())
            throw new CheckProjectException("项目不存在");
        if (FruitDict.ProjectDict.COMPLETE.equals(project.getProjectStatus()))
            throw new CheckProjectException("项目已完成，错误的操作");
        final FruitProjectDao data = FruitProject.getProjectDao();
        data.setUuid(vo.getUuidVo());
        /*使用系统默认时间*/
        data.setEndDate(LocalDateTime.now());
        data.setProjectStatus(FruitDict.ProjectDict.COMPLETE.name());
        data.setStatusDescription(vo.getStatusDescription());
        this.update(data);
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
