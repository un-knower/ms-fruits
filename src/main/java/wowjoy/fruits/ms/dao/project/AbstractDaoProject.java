package wowjoy.fruits.ms.dao.project;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.MessageException;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.list.FruitList;
import wowjoy.fruits.ms.module.mark.UserMarkProject;
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
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.ProjectTeamDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;
import wowjoy.fruits.ms.util.ApplicationContextUtils;
import wowjoy.fruits.ms.util.GsonUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.stream.Collectors.*;

/**
 * Created by wangziwen on 2017/9/6.
 */
public abstract class AbstractDaoProject implements InterfaceDao {

    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    public abstract List<FruitList> findListByProjectId(String projectId);

    public abstract List<FruitTeamUser> findUserByTeamId(ArrayList<String> teamIds);

    /**
     * 1、添加项目信息
     * 2、添加团队关联
     * 3、添加用户关联
     */
    protected abstract void insert(Consumer<FruitProjectDao> daoConsumer);


    protected abstract List<FruitProject> finds(Consumer<FruitProjectExample> exampleConsumer);

    public abstract ArrayList<FruitProjectUser> findUserByProjectIdsAndRole(List<String> ids, ArrayList<FruitDict.UserProjectDict> roles);

    protected abstract List<FruitProjectTeam> findTeamByProjectIds(List<String> ids);

    protected abstract void update(Consumer<FruitProjectDao> daoConsumer, Consumer<FruitProjectExample> exampleConsumer);

    protected abstract void delete(String uuid);

    protected abstract List<FruitPlanUser> findPlanByPlanExampleAndUserIdsAnProjectId(Consumer<FruitPlanExample> exampleConsumer, List<String> userIds, String projectId);

    protected abstract List<FruitTaskUser> findTaskByTaskExampleAndUserIdsAndProjectId(Consumer<FruitTaskExample> exampleConsumer, List<String> userIds, String projectId);

    public abstract List<FruitProjectUser> findAllUserByProjectId(String projectId);

    protected abstract List<FruitProjectDao> findsCurrentUser(Consumer<FruitProjectExample> exampleConsumer);

    public abstract List<FruitTaskProject> myCreateTaskFromProjects();

    protected abstract List<FruitTeamUser> findTeamUserByTeamIds(List<String> teamIds);

    protected abstract List<UserMarkProject> findMarkProject(String userId);

    public abstract void star(String projectId);

    public abstract void unStar(String projectId);

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public final void delete(FruitProjectVo vo) {
        if (!this.finds(example -> example.createCriteria().andUuidEqualTo(vo.getUuidVo()).andIsDeletedEqualTo(Systems.N.name())).stream().findAny().isPresent())
            throw new CheckException(FruitDict.Exception.Check.SYSTEM_NOT_EXISTS.name());
        delete(vo.getUuidVo());
    }

    public final void add(FruitProjectVo vo) {
        try {
            if (StringUtils.isBlank(vo.getTitle()))
                throw new CheckException(FruitDict.Exception.Check.PROJECT_TITLE_NULL.name());
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
        if (!vo.getTeamRelation().isPresent() || !vo.getTeamRelation().get().containsKey(Systems.ADD))
            throw new CheckException(FruitDict.Exception.Check.PROJECT_PRINCIPAL_TEAM_NULL.name());
        Map<String, Long> roleCount = vo.getTeamRelation().get().get(Systems.ADD).parallelStream().collect(groupingBy(ProjectTeamRelation::getTpRole, counting()));
        if (!roleCount.containsKey(ProjectTeamDict.PRINCIPAL.name()) || roleCount.get(ProjectTeamDict.PRINCIPAL.name()) != 1)
            throw new CheckException(FruitDict.Exception.Check.PROJECT_PRINCIPAL_TEAM_NULL.name());
        return this;
    }

    private void addCheckJoinUser(FruitProjectVo vo) {
        if (!vo.getUserRelation().isPresent() || !vo.getUserRelation().get().containsKey(Systems.ADD))
            throw new CheckException(FruitDict.Exception.Check.PROJECT_PRINCIPAL_USER_NULL.name());
        Map<String, Long> roleCount = vo.getUserRelation().get().get(Systems.ADD).parallelStream().collect(groupingBy(UserProjectRelation::getUpRole, counting()));
        if (!roleCount.containsKey(FruitDict.UserProjectDict.PRINCIPAL.name()) || roleCount.get(FruitDict.UserProjectDict.PRINCIPAL.name()) != 1)
            throw new CheckException(FruitDict.Exception.Check.PROJECT_PRINCIPAL_TEAM_NULL.name());
    }

    /**
     * @param vo
     * @return
     */
    public final FruitProject.Info find(FruitProjectVo vo) {
        Optional<FruitProject> project = this.finds(example -> example.createCriteria().andUuidEqualTo(vo.getUuidVo()).andIsDeletedEqualTo(Systems.N.name())).stream().findAny();
        if (!project.isPresent())
            throw new CheckException(FruitDict.Exception.Check.SYSTEM_NOT_EXISTS.name());
        FruitProject.Info exportInfo = GsonUtils.newGson().fromJson(GsonUtils.newGson().toJsonTree(project.get()), TypeToken.of(FruitProject.Info.class).getType());
        CompletableFuture.allOf(
                CompletableFuture.supplyAsync(this.plugUserSupplier(Lists.newArrayList(exportInfo.getUuid()), Lists.newArrayList(FruitDict.UserProjectDict.values())))
                        .thenAccept(userMap -> Optional.ofNullable(userMap.get(exportInfo.getUuid()))
                                .filter(users -> !users.isEmpty())
                                .ifPresent(users -> {
                                    exportInfo.setUsers(users);
                                    exportInfo.seekPrincipalUser();
                                })),
                CompletableFuture.supplyAsync(this.plugTeamSupplier(Lists.newArrayList(project.get().getUuid())))
                        .thenAccept(teamList -> Optional.ofNullable(teamList)
                                .filter(teams -> !teams.isEmpty())
                                .ifPresent(teams -> {
                                    Map<String, ArrayList<FruitTeamUser>> userMap = this.findUserByTeamId(teams.stream().map(FruitProjectTeam::getUuid).collect(toCollection(ArrayList::new))).stream().collect(groupingBy(FruitTeamUser::getTeamId, toCollection(Lists::newArrayList)));
                                    exportInfo.setTeams(teams
                                            .stream()
                                            .map(team -> {
                                                team.setUsers(userMap.get(team.getUuid()));
                                                return team;
                                            })
                                            .collect(toCollection(ArrayList::new)));
                                    exportInfo.seekPrincipalTeam();
                                }))).join();
        return exportInfo;
    }

    public final List<FruitProject.Info> finds(FruitProjectVo vo) {
        final String userId = ApplicationContextUtils.getCurrentUser().getUserId();
        List<FruitProject.Info> exportProject = CompletableFuture.supplyAsync(() -> this.finds(example -> {
            final FruitProjectExample.Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(vo.getTitle()))
                criteria.andTitleLike(MessageFormat.format("%{0}%", vo.getTitle()));
            if (StringUtils.isNotBlank(vo.getProjectStatus()))
                criteria.andProjectStatusEqualTo(vo.getProjectStatus());
            criteria.andIsDeletedEqualTo(Systems.N.name());
            String order = vo.sortConstrue();
            if (StringUtils.isNotBlank(order))
                example.setOrderByClause(order);
            else
                example.setOrderByClause("create_date_time desc");
        })).thenCombine(CompletableFuture.supplyAsync(() -> this.findMarkProject(userId)),
                (projects, markList) -> {
                    List<FruitProject.Info> exportProjects = projects.stream().map(project -> {
                        FruitProject.Info exportInfo = GsonUtils.newGson().fromJson(GsonUtils.newGson().toJsonTree(project), TypeToken.of(FruitProject.Info.class).getType());
                        Optional.of(markList).flatMap(marks -> marks.stream().filter(mark -> mark.getProjectId().equals(project.getUuid())).findAny()).ifPresent(exportInfo::setMarkProject);
                        return exportInfo;
                    }).collect(toList());
                    Map<Boolean, List<FruitProject.Info>> markMap = exportProjects.stream().collect(partitioningBy(FruitProject.Info::isMark));
                    markMap.get(true).sort((l, r) -> Optional.ofNullable(l.getMarkProject()).map(ll -> Optional.ofNullable(r.getMarkProject()).map(rr -> ll.getCreateDateTime().compareTo(rr.getCreateDateTime())).orElse(0)).orElse(0));
                    ArrayList<FruitProject.Info> exportInfo = Lists.newArrayList(markMap.get(true));
                    exportInfo.addAll(markMap.get(false));
                    return exportInfo;
                }).join();
        CompletableFuture.allOf(
                CompletableFuture.supplyAsync(this.plugUserSupplier(exportProject.stream().map(FruitProject::getUuid).collect(toList()), Lists.newArrayList(FruitDict.UserProjectDict.PRINCIPAL)))
                        .thenAccept(userMap -> exportProject.parallelStream()
                                .filter(project -> userMap.containsKey(project.getUuid()))
                                .forEach(project -> {
                                    project.setUsers(userMap.get(project.getUuid()));
                                    project.seekPrincipalUser();
                                })),
                CompletableFuture.supplyAsync(this.plugTeamSupplier(exportProject.stream().map(FruitProject::getUuid).collect(toList())))
                        .thenAccept(teamList -> {
                            Optional.ofNullable(teamList)
                                    .filter(teams -> !teams.isEmpty())
                                    .map(teams -> teams.stream().collect(groupingBy(FruitProjectTeam::getProjectId, toCollection(Lists::newArrayList))))
                                    .ifPresent(teamMap -> exportProject.parallelStream()
                                            .filter(project -> teamMap.containsKey(project.getUuid()))
                                            .forEach(project -> {
                                                project.setTeams(teamMap.get(project.getUuid()));
                                                project.seekPrincipalTeam();
                                            }));
                        })).join();
        return exportProject;
    }

    private Supplier<Map<String, List<FruitProjectUser>>> plugUserSupplier(List<String> projectIds, ArrayList<FruitDict.UserProjectDict> roles) {
        return () -> Optional.ofNullable(projectIds)
                .filter(ids -> !ids.isEmpty())
                .map(ids -> this.findUserByProjectIdsAndRole(ids, roles))
                .map(users -> users.stream()
                        .collect(groupingBy(FruitProjectUser::getProjectId)))
                .orElseGet(Maps::newHashMap);
    }

    private Supplier<List<FruitProjectTeam>> plugTeamSupplier(List<String> projectIds) {
        return () -> Optional.ofNullable(projectIds)
                .filter(ids -> !ids.isEmpty())
                .map(this::findTeamByProjectIds)
                .orElseGet(Lists::newArrayList);
    }

    /**
     * 修改项目信息
     * 贴士：
     * 1、暂时不支持修改项目状态，需修改项目状态需要直接调用complete接口
     *
     * @param vo
     */
    public final void modify(FruitProject.Update vo) {
        if (!this.finds(example -> example.createCriteria().andUuidEqualTo(vo.getUuid())).stream().findAny().isPresent())
            throw new CheckException(FruitDict.Exception.Check.SYSTEM_NOT_EXISTS.name());
        /*检查成员目标、任务完成情况*/
        this.modifyCheckJoinUser(vo);
        this.update(dao -> {
            dao.setUuid(vo.getUuid());
            dao.setTitle(vo.getTitle());
            dao.setDescription(vo.getDescription());
            dao.setPredictStartDate(vo.getPredictStartDate());
            dao.setPredictEndDate(vo.getPredictEndDate());
            dao.setTeamRelation(vo.getTeamRelation());
            dao.setUserRelation(vo.getUserRelation());
        }, example -> example.createCriteria().andUuidEqualTo(vo.getUuid()));
    }

    private void modifyCheckJoinUser(FruitProject.Update vo) {
        /*提取要删除的项目成员*/
        List<String> wantRemoveProjectUserIds = Optional.ofNullable(vo.getUserRelation())
                .map(userJoinMap -> userJoinMap.get(Systems.DELETE))
                .map(users -> users.stream().map(UserProjectRelation::getUserId).collect(toList()))
                .orElseGet(Lists::newArrayList);
        /*待删除团队ids*/
        Optional<List<String>> wantRemoveTeamIds = Optional.ofNullable(vo.getTeamRelation())
                .map(teamJoinMap -> teamJoinMap.get(Systems.DELETE))
                .map(teams -> teams.stream().map(ProjectTeamRelation::getTeamId).distinct().collect(toList()));
        /*查询待删除团队关联用户*/
        List<String> wantRemoveTeamUserIds = wantRemoveTeamIds
                .map(this::findTeamUserByTeamIds)
                .map(users -> users.stream().map(FruitTeamUser::getUserId).collect(toList()))
                .orElseGet(Lists::newArrayList);
        /*查询所有未被移除的项目成员*/
        List<String> noExcludeUser = this.findAllUserByProjectId(vo.getUuid()).stream().map(FruitProjectUser::getUserId).collect(toList());
        noExcludeUser.addAll(Optional.ofNullable(vo.getUserRelation())  //将待添加项目成员合并到未排除用户列表
                .map(userJoinMap -> userJoinMap.get(Systems.ADD))
                .map(users -> users.stream().map(UserProjectRelation::getUserId).collect(toList()))
                .orElseGet(Lists::newArrayList));
        noExcludeUser.addAll(Optional.ofNullable(vo.getTeamRelation())  //将待添加团队用户合并到未排除用户列表
                .map(teamJoinMap -> teamJoinMap.get(Systems.ADD))
                .map(teams -> teams.stream().map(ProjectTeamRelation::getTeamId).distinct().collect(toList()))
                .map(this::findTeamUserByTeamIds)
                .map(users -> users.stream().map(FruitTeamUser::getUserId).collect(toList()))
                .orElseGet(Lists::newArrayList));
        wantRemoveProjectUserIds.addAll(wantRemoveTeamUserIds);     //整合团队成员和项目成员
        Map<String, Long> noExcludeUserMap = noExcludeUser.stream().collect(groupingBy(userId -> userId, counting()));
        Map<String, Long> wantRemoveUserIdMap = wantRemoveProjectUserIds.stream().collect(groupingBy(userId -> userId, counting()));
        LinkedList<String> wantCheckUserIds = Lists.newLinkedList();
        wantRemoveUserIdMap.forEach((userId, count) ->
                Optional.ofNullable(noExcludeUserMap.get(userId))
                        .filter(noCount -> Objects.equals(noCount, count))    /*移除成员次数和总数相等时需要进行移除事项检测*/
                        .map(noCount -> userId)
                        .ifPresent(wantCheckUserIds::add)
        );
        ArrayList<String> userIds = wantCheckUserIds.stream().distinct().collect(toCollection(ArrayList::new));   //去除重复成员
        /*检查是否有删除用户*/
        LinkedList<MessageException.RefuseToRemoveUser> refuseToRemoveUsers = Optional.ofNullable(userIds)
                .filter(ids -> !ids.isEmpty())
                .map(ids -> CompletableFuture.supplyAsync(() -> this.findPlanByPlanExampleAndUserIdsAnProjectId(example -> example.createCriteria()
                        .andIsDeletedEqualTo(Systems.N.name())
                        .andPlanStatusIn(Lists.newArrayList(FruitDict.PlanDict.PENDING.name(), FruitDict.PlanDict.STAY_PENDING.name())), ids, vo.getUuid())
                        .parallelStream().collect(toList()).parallelStream().collect(groupingBy(FruitPlanUser::getUserName, counting()))
                ).thenCombine(CompletableFuture.supplyAsync(() -> this.findTaskByTaskExampleAndUserIdsAndProjectId(
                        example -> example.createCriteria().andIsDeletedEqualTo(Systems.N.name()).andTaskStatusEqualTo(FruitDict.TaskDict.START.name()),
                        ids, vo.getUuid()).parallelStream().collect(groupingBy(FruitTaskUser::getUserName, counting()))
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
                }).join()).orElseGet(LinkedList::new);
        Optional.of(refuseToRemoveUsers).filter(msgList -> !msgList.isEmpty()).ifPresent(msgList -> {
            throw new MessageException(GsonUtils.newGson().toJson(msgList));
        });
    }

    public final void complete(FruitProjectVo vo) {
        Optional<FruitProject> projectDao = this.finds(example -> example.createCriteria().andUuidEqualTo(vo.getUuidVo())).stream().findAny();
        if (!projectDao.isPresent())
            throw new CheckException(FruitDict.Exception.Check.SYSTEM_NOT_EXISTS.name());
        if (FruitDict.ProjectDict.COMPLETE.name().equals(projectDao.get().getProjectStatus()))
            throw new CheckException(FruitDict.Exception.Check.PROJECT_COMPLETE.name());
        this.update(dao -> {
            /*使用系统默认时间*/
            dao.setEndDate(LocalDateTime.now());
            dao.setProjectStatus(FruitDict.ProjectDict.COMPLETE.name());
            dao.setEndDate(LocalDateTime.now());
            dao.setStatusDescription(vo.getStatusDescription());
        }, example -> example.createCriteria().andUuidEqualTo(vo.getUuidVo()));
    }

    private Optional<ArrayList<FruitProjectUser>> findUserByProjectId(String projectId) {
        if (StringUtils.isBlank(projectId))
            throw new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name());
        return Optional.ofNullable(this.findUserByProjectIdsAndRole(Lists.newArrayList(projectId), Lists.newArrayList(FruitDict.UserProjectDict.values())));
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
            criteria.andIsDeletedEqualTo(Systems.N.name());
            String order = vo.sortConstrue();
            if (StringUtils.isNotBlank(order))
                example.setOrderByClause(order);
            else
                example.setOrderByClause("create_date_time desc");
        });
    }
}
