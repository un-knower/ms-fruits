package wowjoy.fruits.ms.dao.project;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.MessageException;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.plan.FruitPlanDao;
import wowjoy.fruits.ms.module.plan.example.FruitPlanExample;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.project.FruitProjectDao;
import wowjoy.fruits.ms.module.project.FruitProjectExample;
import wowjoy.fruits.ms.module.project.FruitProjectVo;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.task.FruitTaskDao;
import wowjoy.fruits.ms.module.task.FruitTaskExample;
import wowjoy.fruits.ms.module.team.FruitTeamDao;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

import static java.util.stream.Collectors.*;

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
    protected abstract void insert(Consumer<FruitProjectDao> daoConsumer);

    protected abstract List<FruitProjectDao> finds(FruitProjectDao dao);

    protected abstract List<FruitProjectDao> findsCurrentUser(FruitProjectDao dao);

    protected abstract Optional<FruitProjectDao> find(Consumer<FruitProjectExample> exampleConsumer);

    protected abstract List<FruitProjectDao> findUserByProjectIds(String... ids);

    protected abstract List<FruitProjectDao> findTeamByProjectIds(String... ids);

    protected abstract void update(Consumer<FruitProjectDao> daoConsumer, Consumer<FruitProjectExample> exampleConsumer);

    protected abstract void delete(String uuid);

    protected abstract List<FruitPlanDao> findPlanByPlanExampleAndUserIdsAnProjectId(Consumer<FruitPlanExample> exampleConsumer, List<String> userIds, String projectId);

    protected abstract List<FruitTaskDao> findTaskByTaskExampleAndUserIdsAndProjectId(Consumer<FruitTaskExample> exampleConsumer, List<String> userIds, String projectId);

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
            if (StringUtils.isBlank(vo.getTitle()))
                throw new CheckException("项目标题不能为空");
            this.addCheckJoinTeam(vo).addCheckJoinUser(vo);
            this.insert(dao -> {
                dao.setUuid(vo.getUuid());
                dao.setTitle(vo.getTitle());
                dao.setPredictStartDate(vo.getPredictStartDate());
                dao.setPredictEndDate(vo.getPredictEndDate());
                dao.setDescription(vo.getDescription());
                dao.setTeamRelation(vo.getTeamRelation().orElseGet(Maps::newLinkedHashMap));
                dao.setUserRelation(vo.getUserRelation().orElseGet(Maps::newLinkedHashMap));
                dao.setProjectStatus(FruitDict.ProjectDict.UNDERWAY.name());
            });
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("添加项目出错");
        }
    }

    private AbstractDaoProject addCheckJoinTeam(FruitProjectVo vo) {
        if (!vo.getTeamRelation().isPresent() || !vo.getTeamRelation().get().containsKey(FruitDict.Systems.ADD))
            throw new CheckException("未检测到负责团队");
        Map<String, Long> roleCount = vo.getTeamRelation().get().get(FruitDict.Systems.ADD).parallelStream().collect(groupingBy(ProjectTeamRelation::getTpRole, counting()));
        if (!roleCount.containsKey(FruitDict.ProjectTeamDict.PRINCIPAL.name()) || roleCount.get(FruitDict.ProjectTeamDict.PRINCIPAL.name()) != 1)
            throw new CheckException("必须绑定负责团队，并且只能绑定一个负责团队");
        return this;
    }

    private void addCheckJoinUser(FruitProjectVo vo) {
        if (!vo.getUserRelation().isPresent() || !vo.getUserRelation().get().containsKey(FruitDict.Systems.ADD))
            throw new CheckException("未检测到负责人");
        Map<String, Long> roleCount = vo.getUserRelation().get().get(FruitDict.Systems.ADD).parallelStream().collect(groupingBy(UserProjectRelation::getUpRole, counting()));
        if (!roleCount.containsKey(FruitDict.UserProjectDict.PRINCIPAL.name()) || roleCount.get(FruitDict.UserProjectDict.PRINCIPAL.name()) != 1)
            throw new CheckException("必须绑定负责人，并且只能绑定一位负责人");
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
        List<FruitProjectDao> result = this.finds(findsTemplate(vo));
        if (!isJoin) return result;
        findJoinInfo(result);
        return result;
    }

    public final List<FruitProjectDao> findsCurrentUser(FruitProjectVo vo) {
        List<FruitProjectDao> result = this.findsCurrentUser(findsTemplate(vo));
        findJoinInfo(result);
        return result;
    }

    private FruitProjectDao findsTemplate(FruitProjectVo vo) {
        FruitProjectDao dao = FruitProject.getDao();
        dao.setTitle(vo.getTitle());
        dao.setUuid(vo.getUuidVo());
        dao.setProjectStatus(vo.getProjectStatus());
        return dao;
    }


    private void findJoinInfo(List<FruitProjectDao> datas) {
        if (datas == null || datas.isEmpty()) return;
        List<String> ids = Lists.newLinkedList();
        datas.forEach((i) -> ids.add(i.getUuid()));
        DaoThread thread = DaoThread.getFixed();
        thread.execute(() -> {
            LinkedHashMap<String, List<FruitUserDao>> users = Maps.newLinkedHashMap();
            this.findUserByProjectIds(ids.toArray(new String[ids.size()])).forEach((i) -> users.put(i.getUuid(), i.getUsers()));
            datas.forEach((i) -> {
                if (!users.containsKey(i.getUuid())) return;
                i.setUsers(users.get(i.getUuid()));
                i.seekPrincipalUser();
            });
            return true;
        });
        thread.execute(() -> {
            LinkedHashMap<String, List<FruitTeamDao>> teams = Maps.newLinkedHashMap();
            this.findTeamByProjectIds(ids.toArray(new String[ids.size()])).forEach((i) -> teams.put(i.getUuid(), i.getTeams()));
            datas.forEach((i) -> {
                if (!teams.containsKey(i.getUuid())) return;
                i.setTeams(teams.get(i.getUuid()));
                i.seekPrincipalTeam();
            });
            return true;
        });
        thread.get();
        thread.shutdown();
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
            if (!this.find(example -> example.createCriteria().andUuidEqualTo(vo.getUuidVo())).isPresent())
                throw new CheckException("不存在的项目");
            this.modifyCheckJoinUser(vo);
            this.update(dao -> {
                dao.setUuid(vo.getUuidVo());
                dao.setTitle(vo.getTitle());
                dao.setDescription(vo.getDescription());
                dao.setPredictStartDate(vo.getPredictStartDate());
                dao.setPredictEndDate(vo.getPredictEndDate());
                dao.setTeamRelation(vo.getTeamRelation().orElseGet(Maps::newLinkedHashMap));
                dao.setUserRelation(vo.getUserRelation().orElseGet(Maps::newLinkedHashMap));
            }, example -> example.createCriteria().andUuidEqualTo(vo.getUuidVo()));
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("修改项目是发生错误");
        }
    }

    private void modifyCheckJoinUser(FruitProjectVo vo) {
        if (!vo.getUserRelation().isPresent()
                || !vo.getUserRelation().get().containsKey(FruitDict.Systems.DELETE)
                || vo.getUserRelation().get().get(FruitDict.Systems.DELETE).isEmpty())
            return;
        /*检查用户有没有待完成的 目标 OR 任务*/
        List<FruitPlanDao> planList = this.findPlanByPlanExampleAndUserIdsAnProjectId(example -> example.createCriteria()
                        .andIsDeletedEqualTo(FruitDict.Systems.N.name())
                        .andPlanStatusIn(Lists.newArrayList(FruitDict.PlanDict.PENDING.name(), FruitDict.PlanDict.STAY_PENDING.name())),
                vo.getUserRelation().get().get(FruitDict.Systems.DELETE)
                        .parallelStream()
                        .map(UserProjectRelation::getUserId)
                        .distinct()
                        .collect(toList()), vo.getUuidVo()
        );

        List<FruitTaskDao> taskList = this.findTaskByTaskExampleAndUserIdsAndProjectId(
                example -> example.createCriteria().andIsDeletedEqualTo(FruitDict.Systems.N.name()).andTaskStatusEqualTo(FruitDict.TaskDict.START.name()),
                vo.getUserRelation().get().get(FruitDict.Systems.DELETE)
                        .parallelStream()
                        .map(UserProjectRelation::getUserId)
                        .distinct()
                        .collect(toList()), vo.getUuidVo()
        );
        Map<String, Long> planUserMap = planList.parallelStream().map(FruitPlanDao::getUsers).collect(reducing((l, r) -> {
            l.addAll(r);
            return l;
        })).orElseGet(Lists::newLinkedList).parallelStream().collect(groupingBy(FruitUserDao::getUserName, counting()));
        Map<String, Long> taskUserMap = taskList.parallelStream().map(FruitTaskDao::getUsers).collect(reducing((l, r) -> {
            l.addAll(r);
            return l;
        })).orElseGet(Lists::newLinkedList).parallelStream().collect(groupingBy(FruitUserDao::getUserName, counting()));
        HashSet<String> userNameSet = planUserMap.keySet().stream().collect(toCollection(Sets::newHashSet));
        userNameSet.addAll(taskUserMap.keySet().stream().collect(toCollection(Sets::newHashSet)));
        if (userNameSet.isEmpty())
            return;
        ArrayList<Object> taskPlanJoin = Lists.newArrayListWithCapacity(2);
        throw new MessageException(userNameSet.parallelStream().map(userName -> {
            taskPlanJoin.clear();
            if (planUserMap.containsKey(userName))
                taskPlanJoin.add(MessageFormat.format("{0}条未完成的目标", planUserMap.get(userName)));
            if (taskUserMap.containsKey(userName))
                taskPlanJoin.add(MessageFormat.format("{0}条未完成的任务", taskUserMap.get(userName)));
            return MessageFormat.format("拒绝移除{0}，此人还有：{1}", userName, StringUtils.join(taskPlanJoin, "、"));
        }).collect(joining("、")));
    }

    public final void complete(FruitProjectVo vo) {
        try {
            Optional<FruitProjectDao> projectDao = this.find(example -> example.createCriteria().andUuidEqualTo(vo.getUuidVo()));
            if (!projectDao.isPresent())
                throw new CheckException("项目不存在");
            if (FruitDict.ProjectDict.COMPLETE.name().equals(projectDao.get().getProjectStatus()))
                throw new CheckException("项目已完成");
            this.update(dao -> {
                /*使用系统默认时间*/
                dao.setEndDate(LocalDateTime.now());
                dao.setProjectStatus(FruitDict.ProjectDict.COMPLETE.name());
                dao.setStatusDescription(vo.getStatusDescription());
            }, example -> example.createCriteria().andUuidEqualTo(vo.getUuidVo()));
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("变更项目状态时出错");
        }
    }

    public final List<FruitUserDao> findUserByProjectId(String projectId) {
        if (StringUtils.isBlank(projectId))
            throw new CheckException("项目id不存在");
        List<FruitProjectDao> result = findUserByProjectIds(projectId);
        if (result.isEmpty())
            throw new CheckException("未查到项目关联用户");
        List<FruitUserDao> users = result.get(0).getUsers();
        HashMap<String, String> echoUser = Maps.newHashMapWithExpectedSize(users.size());
        return users.stream().filter(user -> {
            if (!echoUser.containsKey(user.getUserId())) {
                echoUser.put(user.getUserId(), user.getUserId());
                return true;
            }
            return false;
        }).collect(toList());
    }

}
