package wowjoy.fruits.ms.dao.project;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.list.FruitListVo;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.project.FruitProjectDao;
import wowjoy.fruits.ms.module.project.FruitProjectVo;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
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

    protected abstract List<UserProjectRelation> findJoin(UserProjectRelation relation);

    protected abstract List<ProjectTeamRelation> findJoin(ProjectTeamRelation relation);

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public final void delete(FruitProjectVo vo) {
        try {
            if (!this.findByUUID(vo).isNotEmpty())
                throw new CheckException("项目不存在");
            delete(vo.getUuidVo());
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new CheckException("删除项目出错");
        }
    }

    public final void add(FruitProjectVo vo) {
        try {
            final FruitProjectDao dao = FruitProject.getProjectDao();
            dao.setUuid(vo.getUuid());
            dao.setTitle(vo.getTitle());
            dao.setProjectStatus(vo.getProjectStatus());
            dao.setPredictStartDate(vo.getPredictStartDate());
            dao.setPredictEndDate(vo.getPredictEndDate());
            dao.setDescription(vo.getDescription());
            dao.setTeamRelation(vo.getTeamRelation());
            dao.setUserRelation(vo.getUserRelation());
            dao.setProjectStatus(FruitDict.ProjectDict.UNDERWAY.name());
            this.addCheckJoinTeam(dao).addCheckJoinUser(dao);
            this.insert(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("添加项目出错");
        }
    }

    private AbstractDaoProject addCheckJoinTeam(FruitProjectDao dao) {
        if (dao.getTeamRelation(FruitDict.Systems.ADD).isEmpty())
            throw new CheckException("必须绑定一个负责团队");
        Integer count = 0;
        for (ProjectTeamRelation teamRelation : dao.getTeamRelation(FruitDict.Systems.ADD)) {
            try {
                FruitDict.UserProjectDict.valueOf(teamRelation.getTpRole());
            } catch (Exception e) {
                throw new CheckException("角色不存在");
            }
            if (FruitDict.ProjectTeamDict.PRINCIPAL.name().equals(teamRelation.getTpRole()))
                count++;
        }
        if (count < 1)
            throw new CheckException("没有检测到负责团队角色的团队");
        if (count > 1)
            throw new CheckException("只能尝试绑定一个负责团队");
        return this;
    }

    private void addCheckJoinUser(FruitProjectDao dao) {
        if (dao.getUserRelation(FruitDict.Systems.ADD).isEmpty())
            throw new CheckException("必须绑定一个负责人");
        Integer count = 0;
        for (UserProjectRelation projectRelation : dao.getUserRelation(FruitDict.Systems.ADD)) {
            try {
                FruitDict.UserProjectDict.valueOf(projectRelation.getUpRole());
            } catch (Exception e) {
                throw new CheckException("角色不存在");
            }
            if (FruitDict.UserProjectDict.PRINCIPAL.name().equals(projectRelation.getUpRole()))
                count++;
        }
        if (count < 1)
            throw new CheckException("没有检测到担任负责人角色的用户");
        if (count > 1)
            throw new CheckException("只能尝试绑定一个负责人");
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
    public final void modify(FruitProjectVo vo) {
        try {
            FruitProjectDao project = this.findByUUID(vo);
            if (!project.isNotEmpty())
                throw new CheckException("项目不存在");
            final FruitProjectDao dao = FruitProject.getProjectDao();
            dao.setUuid(vo.getUuidVo());
            dao.setTitle(vo.getTitle());
            dao.setDescription(vo.getDescription());
            dao.setPredictStartDate(vo.getPredictStartDate());
            dao.setPredictEndDate(vo.getPredictEndDate());
            dao.setTeamRelation(vo.getTeamRelation());
            dao.setUserRelation(vo.getUserRelation());
            this.modifyCheckJoinUser(dao).modifyCheckJoinTeam(dao);

            this.update(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("修改项目是发生错误");
        }
    }

    /**
     * 检查修改关联用户的数据
     */
    private AbstractDaoProject modifyCheckJoinUser(FruitProjectDao dao) {
        Integer count = 0;
        if (dao.getUserRelation(FruitDict.Systems.ADD).isEmpty()) return this;
        for (UserProjectRelation userRelation : dao.getUserRelation(FruitDict.Systems.ADD)) {
            try {
                FruitDict.UserProjectDict.valueOf(userRelation.getUpRole());
            } catch (Exception e) {
                throw new CheckException("角色不存在");
            }
            if (FruitDict.UserProjectDict.PRINCIPAL.name().equals(userRelation.getUpRole()))
                count++;
        }
        if (count == 0)
            return this;
        if (count > 1)
            throw new CheckException("只能尝试覆盖一个负责人");
        List<UserProjectRelation> principal = this.findJoin(UserProjectRelation.newInstance(dao.getUuid(), null, FruitDict.UserProjectDict.PRINCIPAL.name()));
        if (principal.isEmpty()) return this;
        List<UserProjectRelation> delete = dao.getUserRelation(FruitDict.Systems.DELETE);
        delete.add(UserProjectRelation.newInstance(null, principal.get(0).getUserId(), principal.get(0).getUpRole()));
        dao.setUserRelation(FruitDict.Systems.DELETE, delete);
        return this;
    }

    /**
     * 检查修改关联团队的数据
     */
    private void modifyCheckJoinTeam(FruitProjectDao dao) {
        Integer count = 0;
        /*是否需要添加团队*/
        if (dao.getTeamRelation(FruitDict.Systems.ADD).isEmpty()) return;
        for (ProjectTeamRelation teamRelation : dao.getTeamRelation(FruitDict.Systems.ADD)) {
            try {
                FruitDict.UserProjectDict.valueOf(teamRelation.getTpRole());
            } catch (Exception e) {
                throw new CheckException("角色不存在");
            }
            if (FruitDict.ProjectTeamDict.PRINCIPAL.name().equals(teamRelation.getTpRole()))
                count++;
        }
        if (count == 0)
            return;
        if (count > 1)
            throw new CheckException("只能尝试覆盖一个负责团队");
        List<ProjectTeamRelation> principal = this.findJoin(ProjectTeamRelation.newInstance(dao.getUuid(), null, FruitDict.ProjectTeamDict.PRINCIPAL.name()));
        /*没有关联项目结束检查*/
        if (principal.isEmpty()) return;
        List<ProjectTeamRelation> delete = dao.getTeamRelation(FruitDict.Systems.DELETE);
        delete.add(ProjectTeamRelation.newInstance(null, principal.get(0).getTeamId(), principal.get(0).getTpRole()));
        dao.setTeamRelation(FruitDict.Systems.DELETE, delete);
    }

    public final void complete(FruitProjectVo vo) {
        try {
            FruitProjectDao project = this.findByUUID(vo);
            if (!project.isNotEmpty())
                throw new CheckException("项目不存在");
            if (FruitDict.ProjectDict.COMPLETE.name().equals(project.getProjectStatus()))
                throw new CheckException("项目已完成，错误的操作");
            final FruitProjectDao data = FruitProject.getProjectDao();
            data.setUuid(vo.getUuidVo());
        /*使用系统默认时间*/
            data.setEndDate(LocalDateTime.now());
            data.setProjectStatus(FruitDict.ProjectDict.COMPLETE.name());
            data.setStatusDescription(vo.getStatusDescription());
            this.update(data);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("变更项目状态时出错");
        }
    }

    public final void insertList(FruitListVo vo) {

    }

}
