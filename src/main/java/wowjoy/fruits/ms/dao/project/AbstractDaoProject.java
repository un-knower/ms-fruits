package wowjoy.fruits.ms.dao.project;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.MessageException;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.list.FruitListDao;
import wowjoy.fruits.ms.module.plan.FruitPlanUser;
import wowjoy.fruits.ms.module.plan.example.FruitPlanExample;
import wowjoy.fruits.ms.module.project.*;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.task.FruitTaskExample;
import wowjoy.fruits.ms.module.task.FruitTaskProject;
import wowjoy.fruits.ms.module.task.FruitTaskUser;
import wowjoy.fruits.ms.module.team.FruitTeamUser;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.ProjectTeamDict;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;

/**
 * Created by wangziwen on 2017/9/6.
 */
public abstract class AbstractDaoProject implements InterfaceDao {

    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    public abstract List<FruitListDao> findListByProjectId(String projectId);

    public abstract List<FruitTeamUser> findUserByTeamId(ArrayList<String> teamIds);

    /**
     * 1、添加项目信息
     * 2、添加团队关联
     * 3、添加用户关联
     */
    protected abstract void insert(Consumer<FruitProjectDao> daoConsumer);


    protected abstract List<FruitProjectDao> finds(Consumer<FruitProjectExample> exampleConsumer);

    protected abstract ArrayList<FruitProjectUser> findUserByProjectIds(Consumer<FruitUserExample> exampleConsumer, List<String> ids);

    protected abstract List<FruitProjectTeam> findTeamByProjectIds(List<String> ids);

    protected abstract void update(Consumer<FruitProjectDao> daoConsumer, Consumer<FruitProjectExample> exampleConsumer);

    protected abstract void delete(String uuid);

    protected abstract List<FruitPlanUser> findPlanByPlanExampleAndUserIdsAnProjectId(Consumer<FruitPlanExample> exampleConsumer, List<String> userIds, String projectId);

    protected abstract List<FruitTaskUser> findTaskByTaskExampleAndUserIdsAndProjectId(Consumer<FruitTaskExample> exampleConsumer, List<String> userIds, String projectId);

    protected abstract List<FruitProjectDao> findsCurrentUser(Consumer<FruitProjectExample> exampleConsumer);

    public abstract List<FruitTaskProject> myCreateTaskFromProjects();

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public final void delete(FruitProjectVo vo) {
        try {
            if (!this.finds(example -> example.createCriteria().andUuidEqualTo(vo.getUuidVo()).andIsDeletedEqualTo(FruitDict.Systems.N.name())).stream().findAny().isPresent())
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
        if (!roleCount.containsKey(ProjectTeamDict.PRINCIPAL.name()) || roleCount.get(ProjectTeamDict.PRINCIPAL.name()) != 1)
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

    /**
     * @param vo
     * @return
     */
    public final FruitProjectDao find(FruitProjectVo vo) {
        Optional<FruitProjectDao> project = this.finds(example -> example.createCriteria().andUuidEqualTo(vo.getUuidVo()).andIsDeletedEqualTo(FruitDict.Systems.N.name())).stream().findAny();
        if (!project.isPresent())
            throw new CheckException("项目不存在");
        DaoThread.getFixed()
                .execute(this.plugUser(Lists.newArrayList(project.get())))
                .execute(this.plugTeam(Lists.newArrayList(project.get())))
                .get().shutdown();
        return project.get();
    }

    public final List<FruitProjectDao> finds(FruitProjectVo vo) {
        List<FruitProjectDao> result = this.finds(example -> {
            final FruitProjectExample.Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(vo.getTitle()))
                criteria.andTitleLike(MessageFormat.format("%{0}%", vo.getTitle()));
            if (StringUtils.isNotBlank(vo.getProjectStatus()))
                criteria.andProjectStatusEqualTo(vo.getProjectStatus());
            criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name());
            String order = vo.sortConstrue();
            if (StringUtils.isNotBlank(order))
                example.setOrderByClause(order);
            else
                example.setOrderByClause("create_date_time desc");
        });
        DaoThread.getFixed()
                .execute(this.plugUser(result))
                .execute(this.plugTeam(result))
                .get().shutdown();
        return result;
    }

    private Callable plugUser(List<FruitProjectDao> projectDaoList) {
        return () -> {
            if (projectDaoList.isEmpty()) return false;
            Map<String, List<FruitProjectUser>> userMap = this.findUserByProjectIds(example -> {
            }, projectDaoList.stream().map(FruitProjectDao::getUuid).collect(toList()))
                    .stream()
                    .collect(groupingBy(FruitProjectUser::getProjectId));
            projectDaoList.parallelStream()
                    .filter(project -> userMap.containsKey(project.getUuid()))
                    .forEach(project -> {
                        project.setUsers(userMap.get(project.getUuid()));
                        project.seekPrincipalUser();
                    });
            return true;
        };
    }

    private Callable plugTeam(List<FruitProjectDao> projectDaoList) {
        return () -> {
            if (projectDaoList.isEmpty()) return false;
            Map<String, ArrayList<FruitProjectTeam>> teamMap = this.findTeamByProjectIds(projectDaoList.stream().map(FruitProjectDao::getUuid).collect(toList()))
                    .stream()
                    .collect(groupingBy(FruitProjectTeam::getProjectId, toCollection(ArrayList::new)));
            projectDaoList.parallelStream()
                    .filter(project -> teamMap.containsKey(project.getUuid()))
                    .forEach(project -> {
                        project.setTeams(teamMap.get(project.getUuid()));
                        project.seekPrincipalTeam();
                    });
            return true;
        };
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
            if (!this.finds(example -> example.createCriteria().andUuidEqualTo(vo.getUuidVo())).stream().findAny().isPresent())
                throw new CheckException("不存在的项目");
            /*检查成员目标、任务完成情况*/
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
        /*检查是否有删除用户*/
        if (!vo.getUserRelation()
                .filter(userRelation -> userRelation.containsKey(FruitDict.Systems.DELETE))
                .filter(userRelation -> !userRelation.get(FruitDict.Systems.DELETE).isEmpty())
                .isPresent())
            return;
        Map<String, List<UserProjectRelation>> userMap = vo.getUserRelation().orElseGet(HashMap::new).get(FruitDict.Systems.DELETE)
                .stream().collect(groupingBy(UserProjectRelation::getUserId));
        Optional.of(CompletableFuture.supplyAsync(() -> {
                    Map<String, Long> collect = this.findPlanByPlanExampleAndUserIdsAnProjectId(example -> example.createCriteria()
                                    .andIsDeletedEqualTo(FruitDict.Systems.N.name())
                                    .andPlanStatusIn(Lists.newArrayList(FruitDict.PlanDict.PENDING.name(), FruitDict.PlanDict.STAY_PENDING.name())),
                            Lists.newArrayList(userMap.keySet().toArray(new String[userMap.keySet().size()])),
                            vo.getUuidVo()
                    ).parallelStream().collect(toList()).parallelStream().collect(groupingBy(FruitPlanUser::getUserName, counting()));
                    return collect;
                }
        ).thenCombine(
                CompletableFuture.supplyAsync(() -> {
                            Map<String, Long> collect = this.findTaskByTaskExampleAndUserIdsAndProjectId(
                                    example -> example.createCriteria().andIsDeletedEqualTo(FruitDict.Systems.N.name()).andTaskStatusEqualTo(FruitDict.TaskDict.START.name()),
                                    Lists.newArrayList(userMap.keySet().toArray(new String[userMap.keySet().size()])), vo.getUuidVo()
                            ).parallelStream().collect(groupingBy(FruitTaskUser::getUserName, counting()));
                            return collect;
                        }
                ), (planLongMap, taskLongMap) -> {
                    HashSet<String> userNameSet = Sets.newHashSet();
                    userNameSet.addAll(planLongMap.keySet().stream().collect(toCollection(Sets::newHashSet)));
                    userNameSet.addAll(taskLongMap.keySet().stream().collect(toCollection(Sets::newHashSet)));
                    return Optional.of(userNameSet).filter(set -> !set.isEmpty()).map(set -> set.parallelStream().map(userName -> MessageException.RefuseToRemoveUser.newInstance(userName,
                            Optional.of(planLongMap).filter(planMap -> planMap.containsKey(userName))
                                    .map(planMap -> planMap.get(userName)).orElse(0L),
                            Optional.of(taskLongMap).filter(taskMap -> taskMap.containsKey(userName))
                                    .map(taskMap -> taskMap.get(userName)).orElse(0L)))
                            .collect(toCollection(LinkedList::new))).orElseGet(LinkedList::new);
                }
        ).join()).filter(msgList -> !msgList.isEmpty()).ifPresent(msgList -> {
            throw new MessageException(new Gson().toJson(msgList));
        });
    }

    public final void complete(FruitProjectVo vo) {
        try {
            Optional<FruitProjectDao> projectDao = this.finds(example -> example.createCriteria().andUuidEqualTo(vo.getUuidVo())).stream().findAny();
            if (!projectDao.isPresent())
                throw new CheckException("项目不存在");
            if (FruitDict.ProjectDict.COMPLETE.name().equals(projectDao.get().getProjectStatus()))
                throw new CheckException("项目已完成");
            this.update(dao -> {
                /*使用系统默认时间*/
                dao.setEndDate(LocalDateTime.now());
                dao.setProjectStatus(FruitDict.ProjectDict.COMPLETE.name());
                dao.setEndDate(LocalDateTime.now());
                dao.setStatusDescription(vo.getStatusDescription());
            }, example -> example.createCriteria().andUuidEqualTo(vo.getUuidVo()));
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("变更项目状态时出错");
        }
    }

    private Optional<ArrayList<FruitProjectUser>> findUserByProjectId(String projectId) {
        if (StringUtils.isBlank(projectId))
            throw new CheckException("项目id不存在");
        return Optional.ofNullable(this.findUserByProjectIds(example -> example.createCriteria().andStatusEqualTo(FruitDict.UserDict.ACTIVE.name()), Lists.newArrayList(projectId)));
    }

    /**
     * 1、查询项目的主团队、协作团队
     * 2、在查询出非主团队和协作团度的团队成员
     *
     * @param projectId
     * @return
     */
    public final Map<ProjectTeamDict, List<FruitUser>> treeTeamUserList(String projectId) {
        /*查询项目团队，查询团队用户*/
        return CompletableFuture.supplyAsync(() -> {
            List<FruitProjectTeam> teamDaoList = this.findTeamByProjectIds(Lists.newArrayList(projectId));
            Map<String, ArrayList<FruitTeamUser>> keyIsTeamUuid = this.findUserByTeamId(teamDaoList
                    .stream()
                    .map(FruitProjectTeam::getUuid).collect(toCollection(ArrayList::new))
            ).parallelStream().collect(groupingBy(FruitTeamUser::getTeamId, toCollection(ArrayList::new)));
            teamDaoList
                    .stream()
                    .filter(fruitTeamDao -> keyIsTeamUuid.containsKey(fruitTeamDao.getUuid()))
                    .forEach(fruitTeamDao -> fruitTeamDao.setUsers(keyIsTeamUuid.get(fruitTeamDao.getUuid())));
            return teamDaoList;
        }).thenCombine(CompletableFuture.supplyAsync(() -> this.findUserByProjectId(projectId)), (teamDaoList, fruitUserDaos) -> {
            Map<ProjectTeamDict, List<FruitUser>> treeTeamUser = Maps.newLinkedHashMap();
            /*提取负责团队、协作团队*/
            Optional<Map<ProjectTeamDict, ArrayList<FruitTeamUser>>> optionalTeamUserMap = Optional.ofNullable(teamDaoList
                    .stream()
                    .collect(toMap(fruitTeamDao -> ProjectTeamDict.valueOf(fruitTeamDao.getProjectRole()), fruitTeamDao -> {
                        ArrayList<FruitTeamUser> userArrayList = Lists.newArrayList();
                        userArrayList.addAll(Optional.ofNullable(fruitTeamDao.getUsers()).orElseGet(ArrayList::new));
                        return userArrayList;
                    }, (l, r) -> {
                        r.addAll(l);
                        return r;
                    })));
            List<FruitTeamUser> principals = optionalTeamUserMap.filter(dictMap -> dictMap.containsKey(ProjectTeamDict.PRINCIPAL))
                    .map(dictMap -> dictMap.get(ProjectTeamDict.PRINCIPAL))
                    .orElseGet(ArrayList::new);
            List<FruitTeamUser> participants = optionalTeamUserMap.filter(dictMap -> dictMap.containsKey(ProjectTeamDict.PARTICIPANT))
                    .map(dictMap -> dictMap.get(ProjectTeamDict.PARTICIPANT))
                    .orElseGet(ArrayList::new);
            /*保留负责团队成员*/
            Map<String, FruitUser> keepUser = principals.stream().collect(toMap(FruitUser::getUserId, fruitUser -> fruitUser));
            Predicate<FruitUser> removeUserPredicate = fruitUser -> {
                if (keepUser.containsKey(fruitUser.getUserId()))
                    return false;
                else
                    keepUser.put(fruitUser.getUserId(), fruitUser);
                return true;
            };
            treeTeamUser.put(ProjectTeamDict.PRINCIPAL, Lists.newArrayList(principals));
            /*移除和负责团队冲突的协作团度成员*/
            treeTeamUser.put(ProjectTeamDict.PARTICIPANT, participants.stream().filter(removeUserPredicate).collect(toCollection(ArrayList::new)));
            /*移除和负责团队、协作团队冲突的其他成员*/
            treeTeamUser.put(ProjectTeamDict.OTHER, fruitUserDaos.orElseGet(ArrayList::new).stream().filter(removeUserPredicate).collect(toCollection(ArrayList::new)));
            return treeTeamUser;
        }).join();
    }

    /*************
     * 当前用户
     *************/

    public final List<FruitProjectDao> findsCurrentUser(FruitProjectVo vo) {
        return this.findsCurrentUser(example -> {
            final FruitProjectExample.Criteria criteria = example.createCriteria();
            criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name());
            String order = vo.sortConstrue();
            if (StringUtils.isNotBlank(order))
                example.setOrderByClause(order);
            else
                example.setOrderByClause("create_date_time desc");
        });
    }

}
