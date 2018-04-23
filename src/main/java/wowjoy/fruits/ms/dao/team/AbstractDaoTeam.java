package wowjoy.fruits.ms.dao.team;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.MessageException;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.plan.FruitPlanUser;
import wowjoy.fruits.ms.module.plan.example.FruitPlanExample;
import wowjoy.fruits.ms.module.relation.entity.UserTeamRelation;
import wowjoy.fruits.ms.module.task.FruitTaskExample;
import wowjoy.fruits.ms.module.task.FruitTaskUser;
import wowjoy.fruits.ms.module.team.*;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;
import wowjoy.fruits.ms.util.ApplicationContextUtils;
import wowjoy.fruits.ms.util.GsonUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    public abstract List<UserTeamRelation> findJoinTeamByUserId(String userId);

    protected abstract void insert(FruitTeamDao dao);

    protected abstract void update(FruitTeamDao data);

    protected abstract void delete(FruitTeamDao data);

    public abstract List<FruitPlanUser> findUserByPlanExampleAndUserId(Consumer<FruitPlanExample> exampleConsumer, ArrayList<String> userIds);

    public abstract List<FruitTaskUser> findUserByTaskExampleAndUserId(Consumer<FruitTaskExample> exampleConsumer, ArrayList<String> userIds);

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
            teamDaoList.parallelStream().forEach(team -> team.findUsers().orElseGet(ArrayList::new).sort((l, r) -> l.getTeamRole().equals(FruitDict.UserTeamDict.LEADER.name()) ? -1 : 1));
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
            });
            return true;
        };
    }

    public Supplier<List<FruitTeamUser>> plugUserSupplier(List<String> teamId, Consumer<FruitUserExample> userExampleConsumer) {
        return () -> this.findUserByTeamIds(teamId, userExampleConsumer);
    }

    public final List<FruitTeamDao> findCurrent() {
        List<UserTeamRelation> userTeamList = this.findJoinTeamByUserId(ApplicationContextUtils.getCurrentUser().getUserId());
        if (userTeamList.isEmpty()) return Lists.newLinkedList();
        return this.findTeamByExample(fruitTeamExample -> fruitTeamExample.createCriteria().andUuidIn(userTeamList.parallelStream().map(UserTeamRelation::getUuid).collect(toList())));
    }

    public final FruitTeamDao findInfo(String uuid) {
        return Optional.ofNullable(findInfo(uuid, example -> example.createCriteria().andIsDeletedEqualTo(Systems.N.name()))).map(info -> {
            info.searchLeader();
            return info;
        }).orElse(null);
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
            Optional.ofNullable(vo.getUserRelation().get(Systems.ADD))
                    .map(users -> users.stream().collect(groupingBy(UserTeamRelation::getUserId, counting())))
                    .ifPresent(addUser -> addUser.forEach((id, count) -> {
                        Optional.of(count).filter(i -> i <= 1).orElseThrow(() -> new CheckException("一个成员不可重复添加"));
                    }));
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
            this.checkWantAddUserIfRepeat(vo.getUserRelation().get(Systems.ADD), vo.getUuidVo());
            this.checkWantDeleteUserPendingCompleteItem(vo.getUserRelation().get(Systems.DELETE));
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

    /*检查用户是否重复添加*/
    public void checkWantAddUserIfRepeat(List<UserTeamRelation> addUserList, String teamId) {
        Map<String, Long> teamUserMap = this.findUserByTeamIds(Lists.newArrayList(teamId), example -> example.createCriteria().andIsDeletedEqualTo(Systems.N.name()))
                .stream().collect(groupingBy(FruitTeamUser::getUserId, counting()));
            /*检查添加人员中是否有重复用户*/
        Optional.ofNullable(addUserList)
                .map(users -> users.stream().collect(groupingBy(UserTeamRelation::getUserId, counting())))
                .ifPresent(addUser -> addUser.forEach((id, count) -> {
                    Optional.of(count).filter(i -> i <= 1).orElseThrow(() -> new CheckException("一个成员不可重复添加"));
                    Optional.of(teamUserMap).filter(userMap -> !userMap.containsKey(id)).orElseThrow(() -> new CheckException("已有相同成员，不可重复添加"));
                }));
    }

    /*检查等待删除的用户是否有未完成的事项*/
    public void checkWantDeleteUserPendingCompleteItem(List<UserTeamRelation> deleteUserList) {
        /*检查移除人员中是否有未完成的事项*/
        Optional.ofNullable(deleteUserList)
                .filter(users -> !users.isEmpty())
                .map(users -> users.stream().map(UserTeamRelation::getUserId).collect(toCollection(ArrayList::new)))
                .ifPresent(userIds -> {
                    Optional.of(CompletableFuture.supplyAsync(() -> this.findUserByPlanExampleAndUserId(example -> example.createCriteria()
                            .andIsDeletedEqualTo(Systems.N.name())
                            .andPlanStatusIn(Lists.newArrayList(FruitDict.PlanDict.PENDING.name(), FruitDict.PlanDict.STAY_PENDING.name())), userIds).stream().collect(groupingBy(FruitPlanUser::getUserName, counting()))
                    ).thenCombine(CompletableFuture.supplyAsync(() ->
                                    this.findUserByTaskExampleAndUserId(example -> example.createCriteria().andIsDeletedEqualTo(Systems.N.name()).andTaskStatusEqualTo(FruitDict.TaskDict.START.name()), userIds)
                                            .stream().collect(groupingBy(FruitTaskUser::getUserName, counting()))),
                            (planUserMap, taskUserMap) -> {
                                HashSet<String> userNameSet = Sets.newHashSet();
                                userNameSet.addAll(planUserMap.keySet().stream().collect(toCollection(Sets::newHashSet)));
                                userNameSet.addAll(taskUserMap.keySet().stream().collect(toCollection(Sets::newHashSet)));
                                return Optional.of(userNameSet).filter(set -> !set.isEmpty()).map(set -> set.parallelStream().map(userName -> MessageException.RefuseToRemoveUser.newInstance(userName,
                                        Optional.of(planUserMap).filter(planMap -> planMap.containsKey(userName))
                                                .map(planMap -> planMap.get(userName)).orElse(0L),
                                        Optional.of(taskUserMap).filter(taskMap -> taskMap.containsKey(userName))
                                                .map(taskMap -> taskMap.get(userName)).orElse(0L)))
                                        .collect(toCollection(LinkedList::new))).orElseGet(LinkedList::new);
                            }).join()).filter(msgList -> !msgList.isEmpty()).ifPresent(msgList -> {
                        throw new MessageException(GsonUtils.newGson().toJson(msgList));
                    });
                });
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
