package wowjoy.fruits.ms.dao.project;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.project.FruitProjectDao;
import wowjoy.fruits.ms.module.project.FruitProjectVo;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.team.FruitTeamDao;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
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

    protected abstract List<FruitProjectDao> finds(FruitProjectDao dao);

    protected abstract FruitProject find(FruitProjectDao dao);

    protected abstract List<FruitProjectDao> findUserByProjectIds(String... ids);

    protected abstract List<FruitProjectDao> findTeamByProjectIds(String... ids);

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
            this.findByUUID(vo.getUuidVo()).isNotEmpty();
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
            final FruitProjectDao dao = FruitProject.getDao();
            dao.setUuid(vo.getUuid());
            dao.setTitle(vo.getTitle());
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

    @Deprecated
    public final List<FruitProjectDao> findRelation(FruitProjectVo vo) {
        return this.finds(vo, true);
    }

    public final FruitProjectDao findByUUID(FruitProjectVo vo, boolean isJoin) {
        List<FruitProjectDao> finds = this.finds(vo, isJoin);
        if (finds.isEmpty())
            throw new CheckException("项目不存在");
        return finds.get(0);
    }

    public final FruitProjectDao findByUUID(String uuid) {
        FruitProjectVo vo = FruitProject.getVo();
        vo.setUuidVo(uuid);
        return findByUUID(vo, false);
    }

    public final List<FruitProjectDao> finds(FruitProjectVo vo, boolean isJoin) {
        FruitProjectDao dao = FruitProject.getDao();
        dao.setTitle(vo.getTitle());
        dao.setUuid(vo.getUuidVo());
        dao.setProjectStatus(vo.getProjectStatus());
        List<FruitProjectDao> result = this.finds(dao);
        if (!isJoin) return result;
        List<String> ids = Lists.newLinkedList();
        result.forEach((i) -> ids.add(i.getUuid()));
        DaoThread thread = DaoThread.getInstance();
        thread.execute(() -> {
            LinkedHashMap<String, List<FruitUserDao>> users = Maps.newLinkedHashMap();
            this.findUserByProjectIds(ids.toArray(new String[ids.size()])).forEach((i) -> users.put(i.getUuid(), i.getUsers()));
            result.forEach((i) -> {
                if (!users.containsKey(i.getUuid())) return;
                i.setUsers(users.get(i.getUuid()));
                i.seekPrincipalUser();
            });
            return true;
        });
        thread.execute(() -> {
            LinkedHashMap<String, List<FruitTeamDao>> teams = Maps.newLinkedHashMap();
            this.findTeamByProjectIds(ids.toArray(new String[ids.size()])).forEach((i) -> teams.put(i.getUuid(), i.getTeams()));
            result.forEach((i) -> {
                if (!teams.containsKey(i.getUuid())) return;
                i.setTeams(teams.get(i.getUuid()));
                i.seekPrincipalTeam();
            });
            return true;
        });
        thread.get();
        thread.shutdown();
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
            this.findByUUID(vo.getUuidVo());
            final FruitProjectDao dao = FruitProject.getDao();
            dao.setUuid(vo.getUuidVo());
            dao.setTitle(vo.getTitle());
            dao.setDescription(vo.getDescription());
            dao.setPredictStartDate(vo.getPredictStartDate());
            dao.setPredictEndDate(vo.getPredictEndDate());
            dao.setTeamRelation(vo.getTeamRelation());
            dao.setUserRelation(vo.getUserRelation());
            this.update(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("修改项目是发生错误");
        }
    }

    public final void complete(FruitProjectVo vo) {
        try {
            FruitProjectDao project = this.findByUUID(vo.getUuidVo());
            if (FruitDict.ProjectDict.COMPLETE.name().equals(project.getProjectStatus()))
                throw new CheckException("项目已完成，错误的操作");
            final FruitProjectDao data = FruitProject.getDao();
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

    public List<FruitUserDao> findUserByProjectId(String projectId) {
        if (StringUtils.isBlank(projectId))
            throw new CheckException("项目id不存在");
        List<FruitProjectDao> result = findUserByProjectIds(projectId);
        if (result.isEmpty())
            throw new CheckException("未查到项目关联用户");
        return result.get(0).getUsers();
    }

}
