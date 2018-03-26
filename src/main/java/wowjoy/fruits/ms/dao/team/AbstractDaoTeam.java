package wowjoy.fruits.ms.dao.team;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.relation.entity.UserTeamRelation;
import wowjoy.fruits.ms.module.team.*;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static java.util.stream.Collectors.*;

/**
 * Created by wangziwen on 2017/9/6.
 */
public abstract class AbstractDaoTeam implements InterfaceDao {

    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    public abstract List<FruitTeamUser> findUserByTeamIds(List<String> teamIds, Consumer<FruitUserExample> userExampleConsumer);

    public abstract List<FruitTeamDao> findTeamByExample(Consumer<FruitTeamExample> teamExampleConsumer);

    public abstract List<UserTeamRelation> findUserTeam(String userId);

    protected abstract void insert(FruitTeamDao dao);

    protected abstract void update(FruitTeamDao data);

    protected abstract void delete(FruitTeamDao data);

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public final List<FruitTeamDao> findTeams(FruitTeamVo vo) {
        List<FruitTeamDao> teamDaoList = this.findTeamByExample(fruitTeamExample -> {
            FruitTeamExample.Criteria criteria = fruitTeamExample.createCriteria();
            if (StringUtils.isNotBlank(vo.getTitle()))
                criteria.andTitleLike(MessageFormat.format("%{0}%", vo.getTitle()));
            if (StringUtils.isNotBlank(vo.getUuidVo()))
                criteria.andUuidEqualTo(vo.getUuidVo());
            criteria.andIsDeletedEqualTo(Systems.N.name());
            String sort = vo.sortConstrue();
            if (StringUtils.isNotBlank(sort))
                fruitTeamExample.setOrderByClause(sort);
            else
                fruitTeamExample.setOrderByClause("create_date_time desc");
        });
        try {
            this.plugUser(teamDaoList).call();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CheckException("获取用户信息异常");
        }
        return teamDaoList;
    }

    private Callable plugUser(List<FruitTeamDao> teamDaoList) {
        return plugUser(teamDaoList, example -> example.createCriteria().andIsDeletedEqualTo(Systems.N.name()));
    }

    private Callable plugUser(List<FruitTeamDao> teamDaoList, Consumer<FruitUserExample> userExampleConsumer) {
        return () -> {

            Map<String, ArrayList<FruitTeamUser>> userMap = this.findUserByTeamIds(teamDaoList.parallelStream().map(FruitTeamDao::getUuid).collect(toList()), userExampleConsumer)
                    .parallelStream().collect(groupingBy(FruitTeamUser::getTeamId, toCollection(ArrayList::new)));
            teamDaoList.parallelStream().forEach(fruitTeamDao -> {
                fruitTeamDao.setUsers(userMap.get(fruitTeamDao.getUuid()));
                fruitTeamDao.searchLeader();
            });
            return true;
        };
    }

    public final List<FruitTeamDao> findCurrent() {
        List<UserTeamRelation> userTeamList = this.findUserTeam(ApplicationContextUtils.getCurrentUser().getUserId());
        if (userTeamList.isEmpty()) return Lists.newLinkedList();
        return this.findTeamByExample(fruitTeamExample -> fruitTeamExample.createCriteria().andUuidIn(userTeamList.parallelStream().map(UserTeamRelation::getUuid).collect(toList())));
    }

    public final FruitTeamDao findInfo(String uuid) {
        return findInfo(uuid, example -> example.createCriteria().andIsDeletedEqualTo(Systems.N.name()));
    }

    public final FruitTeamDao findInfo(String uuid, Consumer<FruitUserExample> userExampleConsumer) {
        if (StringUtils.isBlank(uuid))
            throw new CheckException("团队id不能为空");
        List<FruitTeamDao> result = this.findTeamByExample(fruitTeamExample -> {
            FruitTeamExample.Criteria criteria = fruitTeamExample.createCriteria();
            if (StringUtils.isNotBlank(uuid))
                criteria.andUuidEqualTo(uuid);
            criteria.andIsDeletedEqualTo(Systems.N.name());
        });
        if (result.isEmpty())
            throw new CheckException("未找到指定团队");
        try {
            this.plugUser(result, userExampleConsumer).call();
        } catch (Exception e) {
            throw new CheckException("获取团队用户信息失败");
        }
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
        if (dao.getUserRelation(Systems.ADD).isEmpty())
            throw new CheckException("缺少关联用户");
        Map<String, List<UserTeamRelation>> roleValue = Maps.newLinkedHashMap();
        dao.getUserRelation(Systems.ADD).forEach((i) -> {
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
