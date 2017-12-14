package wowjoy.fruits.ms.dao.team;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.relation.entity.UserTeamRelation;
import wowjoy.fruits.ms.module.team.FruitTeam;
import wowjoy.fruits.ms.module.team.FruitTeamDao;
import wowjoy.fruits.ms.module.team.FruitTeamVo;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by wangziwen on 2017/9/6.
 */
public abstract class AbstractDaoTeam implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    public abstract List<FruitTeamDao> findRelation(FruitTeamDao dao, FruitUserDao userDao);

    protected abstract void insert(FruitTeamDao dao);

    protected abstract void update(FruitTeamDao data);

    protected abstract void delete(FruitTeamDao data);

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public final List<FruitTeamDao> findRelation(FruitTeamVo vo) {
        FruitTeamDao dao = FruitTeam.getDao();
        dao.setTitle(vo.getTitle());
        dao.setUuid(vo.getUuidVo());
        FruitUserDao user = FruitUser.getDao();
        if (StringUtils.isNotBlank(vo.getUserName()))
            user.setUserName(vo.getUserName());
        List<FruitTeamDao> result = this.findRelation(dao, user);
        /*检索团队leader*/
        result.forEach((i) -> i.searchLeader());
        return result;
    }

    public final List<FruitTeamDao> findCurrent(FruitTeamVo vo) {
        FruitTeamDao dao = FruitTeam.getDao();
        dao.setTitle(vo.getTitle());
        dao.setUuid(vo.getUuidVo());
        FruitUserDao user = FruitUser.getDao();
        user.setUserId(ApplicationContextUtils.getCurrentUser().getUserId());
        if (StringUtils.isNotBlank(vo.getUserName()))
            user.setUserName(vo.getUserName());
        List<FruitTeamDao> result = this.findRelation(dao, user);
        /*检索团队leader*/
        result.forEach((i) -> i.searchLeader());
        return result;
    }

    public final FruitTeamDao find(FruitTeamVo vo) {
        FruitTeamDao dao = FruitTeam.getDao();
        dao.setUuid(vo.getUuidVo());
        List<FruitTeamDao> result = this.findRelation(dao, FruitUser.getDao());
        if (result.isEmpty())
            throw new CheckException("未找到指定团队");
        result.forEach((i) -> i.searchLeader());
        return result.get(0);
    }

    public final void insert(FruitTeamVo vo) {
        try {
            FruitTeamDao dao = FruitTeam.getDao();
            dao.setUuid(vo.getUuid());
            dao.setTitle(vo.getTitle());
            dao.setDescription(vo.getDescription());
            dao.setUserRelation(vo.getUserRelation());
            this.checkInUsers(dao);
            this.insert(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("团队添加数据错误");
        }
    }

    private void checkInUsers(FruitTeamDao dao) {
        if (dao.getUserRelation(FruitDict.Systems.ADD).isEmpty())
            throw new CheckException("缺少关联用户");
        Map<String, List<UserTeamRelation>> roleValue = Maps.newLinkedHashMap();
        dao.getUserRelation(FruitDict.Systems.ADD).forEach((i) -> {
            if (roleValue.containsKey(i.getUtRole()))
                roleValue.get(i.getUtRole()).add(i);
            else
                roleValue.put(i.getUtRole(), Lists.newArrayList(i));
        });
        if (!roleValue.containsKey(FruitDict.UserTeamDict.LEADER.name())) throw new CheckException("团队leader不存在");
        if (roleValue.get(FruitDict.UserTeamDict.LEADER.name()).size() > 1) throw new CheckException("只允许添加一位leader");
    }

    public final void update(FruitTeamVo vo) {
        try {
            if (StringUtils.isBlank(vo.getUuidVo()))
                throw new CheckException("Team id not is null");
            FruitTeamDao dao = FruitTeam.getDao();
            dao.setUuid(vo.getUuidVo());
            dao.setUserRelation(vo.getUserRelation());
            dao.setTitle(vo.getTitle());
            dao.setDescription(vo.getDescription());
            this.update(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("团队修改[" + vo.getUuidVo() + "]错误");
        }

    }

    public final void delete(String uuid) {
        try {
            if (StringUtils.isBlank(uuid))
                throw new CheckException("Team id not is null");
            FruitTeamDao dao = FruitTeam.getDao();
            dao.setUuid(uuid);
            this.delete(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("团队删除[" + uuid + "]错误");
        }

    }

}
