package wowjoy.fruits.ms.dao.plan;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.logs.service.ServiceLogs;
import wowjoy.fruits.ms.dao.relation.impl.PlanProjectDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.PlanUserDaoImpl;
import wowjoy.fruits.ms.dao.task.AbstractDaoTask;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.logs.FruitLogsDao;
import wowjoy.fruits.ms.module.logs.FruitLogsVo;
import wowjoy.fruits.ms.module.plan.*;
import wowjoy.fruits.ms.module.plan.example.FruitPlanExample;
import wowjoy.fruits.ms.module.plan.example.FruitPlanSummaryExample;
import wowjoy.fruits.ms.module.plan.mapper.FruitPlanMapper;
import wowjoy.fruits.ms.module.plan.mapper.FruitPlanSummaryMapper;
import wowjoy.fruits.ms.module.relation.entity.PlanProjectRelation;
import wowjoy.fruits.ms.module.relation.entity.PlanUserRelation;
import wowjoy.fruits.ms.module.relation.example.PlanProjectRelationExample;
import wowjoy.fruits.ms.module.relation.example.PlanUserRelationExample;
import wowjoy.fruits.ms.module.task.FruitTaskExample;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static java.util.stream.Collectors.*;

/**
 * Created by wangziwen on 2017/8/25.
 */
@Service
@Transactional
public class PlanDaoImpl extends AbstractDaoPlan {

    private final FruitPlanMapper mapper;
    private final FruitPlanSummaryMapper summaryMapper;
    private final PlanUserDaoImpl<PlanUserRelation, PlanUserRelationExample> userRelation;
    private final PlanProjectDaoImpl<PlanProjectRelation, PlanProjectRelationExample> projectDao;
    private final ServiceLogs logsDaoImpl;
    private final AbstractDaoTask daoTask;

    @Autowired
    public PlanDaoImpl(FruitPlanMapper mapper, FruitPlanSummaryMapper summaryMapper, @Qualifier("planUserDaoImpl") PlanUserDaoImpl userRelation, @Qualifier("planProjectDaoImpl") PlanProjectDaoImpl projectDao, ServiceLogs logsDaoImpl, AbstractDaoTask daoTask) {
        this.mapper = mapper;
        this.summaryMapper = summaryMapper;
        this.userRelation = userRelation;
        this.projectDao = projectDao;
        this.logsDaoImpl = logsDaoImpl;
        this.daoTask = daoTask;
    }

    @Override
    protected List<FruitPlanDao> findByProjectId(Consumer<FruitPlanExample> exampleConsumer, String projectId, Integer pageNum, Integer pageSize, boolean isPage) {
        FruitPlanExample example = new FruitPlanExample();
        exampleConsumer.accept(example);
        if (isPage) PageHelper.startPage(pageNum, pageSize);
        return mapper.selectByProjectId(example, projectId);
    }

    @Override
    public List<FruitPlanDao> findByExample(Consumer<FruitPlanExample> exampleConsumer) {
        FruitPlanExample example = new FruitPlanExample();
        exampleConsumer.accept(example);
        return mapper.selectByExample(example);
    }

    public List<FruitPlanDao> findByExampleAndUserIdAndProjectId(Consumer<FruitPlanExample> exampleConsumer, String projectId, List<String> userIds) {
        FruitPlanExample example = new FruitPlanExample();
        exampleConsumer.accept(example);
        return mapper.selectByExampleAndUserIdAndProjectId(example, projectId, userIds);
    }

    @Override
    protected List<FruitPlanDao> findUserByPlanIds(List<String> planIds, String currentUserId) {
        if (planIds == null || planIds.isEmpty()) return Lists.newLinkedList();
        return mapper.selectUserByPlanIds(planIds, currentUserId);
    }

    @Override
    protected Optional<Map<String, ArrayList<FruitPlanTask>>> findTaskByTaskExampleAndPlanIds(Consumer<FruitTaskExample> taskConsumer, List<String> planIds) {
        Optional<List<FruitPlanTask>> optionalTask = Optional.ofNullable(planIds)
                .filter(planList -> !planList.isEmpty())
                .map(planList -> {
                    FruitTaskExample example = new FruitTaskExample();
                    taskConsumer.accept(example);
                    return mapper.selectTaskByPlanIds(example, planIds);
                });
        return optionalTask.filter(tasks -> !tasks.isEmpty())
                .map(tasks -> CompletableFuture.supplyAsync(daoTask.plugUserSupplier(tasks)))
                .map(CompletableFuture::join)
                .flatMap(optionalUserByTaskId -> {
                    optionalTask.ifPresent(tasks -> tasks.forEach(task -> {
                        task.setUsers(optionalUserByTaskId.filter(userByTaskId -> userByTaskId.containsKey(task.getUuid())).map(userByTaskId -> userByTaskId.get(task.getUuid())).orElseGet(LinkedList::new));
                    }));
                    return optionalTask;
                }).map(tasks -> tasks.stream().collect(groupingBy(FruitPlanTask::getPlanId, toCollection(ArrayList::new))));
    }

    @Override
    protected Map<String, LinkedList<FruitLogsDao>> findLogsByPlanIds(List<String> planIds) {
        return logsDaoImpl.findLogs(example -> {
            example
                    .createCriteria().andFruitUuidIn(planIds)
                    .andFruitTypeEqualTo(FruitDict.Parents.PLAN.name());
            example.setOrderByClause("flogs.create_date_time desc");
        }, FruitDict.Parents.PLAN);
    }

    @Override
    public void insertLogs(Consumer<FruitLogsVo> vo) {
        logsDaoImpl.insertVo(vo);
    }

    @Override
    protected Optional<FruitPlanDao> findByUUID(String uuid) {
        if (StringUtils.isBlank(uuid))
            throw new CheckException("【计划】uuId不能为空");
        FruitPlanExample example = new FruitPlanExample();
        example.createCriteria().andUuidEqualTo(uuid).andIsDeletedEqualTo(Systems.N.name());
        return mapper.selectByExampleWithBLOBs(example).stream().findAny();
    }

    /**
     * 关联信息：
     * 1、项目关联
     * 2、责任人关联
     *
     * @param dao
     */
    @Override
    public void insert(FruitPlan.Insert dao) {
        mapper.insertSelective(dao);
        /*添加用户关联信息*/
        Optional.ofNullable(dao.getUserRelation())
                .filter(userMap -> userMap.containsKey(Systems.ADD))
                .map(userMap -> userMap.get(Systems.ADD))
                .orElseGet(ArrayList::new)
                .forEach(user -> userRelation.insert(planUser -> {
                    planUser.setUserId(user);
                    planUser.setPlanId(dao.getUuid());
                }));
        /*添加项目关联信息*/
        Optional.ofNullable(dao.getProjectRelation())
                .filter(projectMap -> projectMap.containsKey(Systems.ADD))
                .map(projectMap -> projectMap.get(Systems.ADD))
                .orElseGet(ArrayList::new)
                .forEach(project -> projectDao.insert(projectPlan -> {
                    projectPlan.setPlanId(dao.getUuid());
                    projectPlan.setProjectId(project);
                }));
    }

    @Override
    public void update(Consumer<FruitPlan.Update> planDaoConsumer, Consumer<FruitPlanExample> exampleConsumer) {
        final FruitPlanExample example = new FruitPlanExample();
        final FruitPlan.Update dao = FruitPlan.newUpdate();
        planDaoConsumer.accept(dao);
        exampleConsumer.accept(example);
        Optional.of(example.getOredCriteria())
                /*检查列表元素是否为空*/
                .map(criteriaList -> criteriaList.stream().filter(FruitPlanExample.Criteria::isValid).collect(toList()))
                .filter(criteriaList -> !criteriaList.isEmpty())
                .orElseThrow(() -> new CheckException("修改计划时，必须携带条件"));
        /*更新用户关联信息*/
        Optional.ofNullable(dao.getUserRelation())
                .filter(userMap -> userMap.containsKey(Systems.DELETE))
                .map(userMap -> userMap.get(Systems.DELETE))
                .orElseGet(ArrayList::new)
                .forEach(user -> userRelation.deleted(userRelationExample -> userRelationExample.createCriteria().andUserIdEqualTo(user).andPlanIdEqualTo(dao.getUuid())));
        Optional.ofNullable(dao.getUserRelation())
                .filter(userMap -> userMap.containsKey(Systems.ADD))
                .map(userMap -> userMap.get(Systems.ADD))
                .orElseGet(ArrayList::new)
                .forEach(user -> userRelation.insert(planUserRelation -> {
                    planUserRelation.setPlanId(dao.getUuid());
                    planUserRelation.setUserId(user);
                }));
        mapper.updateByExampleSelective(dao, example);
    }

    @Override
    public Optional<List<FruitPlanDao>> batchUpdateStatusAndReturnResult(Consumer<FruitPlan.Update> planDaoConsumer, Consumer<FruitPlanExample> fruitPlanExampleConsumer) {
        Optional<List<FruitPlanDao>> optionalPlans = Optional.ofNullable(this.findByExample(fruitPlanExampleConsumer));
        optionalPlans.filter(planList -> !planList.isEmpty()).ifPresent(planList -> this.update(planDaoConsumer, example -> example.createCriteria().andUuidIn(planList.stream().map(FruitPlanDao::getUuid).collect(toList()))));
        return optionalPlans;
    }

    @Override
    public void delete(String uuid) {
        FruitPlanExample example = new FruitPlanExample();
        example.createCriteria().andUuidEqualTo(uuid);
        FruitPlanDao delete = FruitPlan.getDao();
        delete.setIsDeleted(Systems.Y.name());
        mapper.updateByExampleSelective(delete, example);
        FruitPlanDao plan = FruitPlan.getDao();
        plan.setUuid(uuid);
        userRelation.deleted(userRelationExample -> userRelationExample.createCriteria().andPlanIdEqualTo(uuid));
        projectDao.deleted(projectRelationExample -> projectRelationExample.createCriteria().andPlanIdEqualTo(uuid));
        /*删除进度小结*/
        deleteSummarys(FruitPlanSummary.newDao(uuid));
    }

    @Override
    public void deleteSummarys(FruitPlanSummaryDao dao) {
        FruitPlanSummaryExample example = new FruitPlanSummaryExample();
        FruitPlanSummaryExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getPlanId()))
            criteria.andPlanIdEqualTo(dao.getPlanId());
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckException("【删除进度小结】没有可用的删除条件");
        FruitPlanSummaryDao delete = FruitPlanSummary.getDao();
        delete.setIsDeleted(Systems.Y.name());
        summaryMapper.updateByExampleSelective(delete, example);
    }

    @Deprecated
    private static class Relation {
        private final PlanUserDaoImpl userDao;
        private final PlanProjectDaoImpl projectdao;
        private final FruitPlanDao planDao;


        private Relation(PlanUserDaoImpl userDao, PlanProjectDaoImpl projectdao, FruitPlanDao planDao) {
            this.userDao = userDao;
            this.projectdao = projectdao;
            this.planDao = planDao;
        }

        public static Relation getInstance(PlanUserDaoImpl userDao, PlanProjectDaoImpl projectdao, FruitPlanDao planDao) {
            return new Relation(userDao, projectdao, planDao);
        }

        public void insertProject() {
            planDao.getProjectRelation(Systems.ADD).forEach((i) -> {
                projectdao.insert(PlanProjectRelation.newInstance(planDao.getUuid(), i));
            });
        }

        public Relation removeProject() {
            planDao.getProjectRelation(Systems.DELETE).forEach((i) ->
                    projectdao.deleted(PlanProjectRelation.newInstance(planDao.getUuid(), StringUtils.isBlank(i) ? null : i))
            );
            return this;
        }

        public Relation removesProject() {
            projectdao.deleted(PlanProjectRelation.newInstance(planDao.getUuid()));
            return this;
        }

    }

}
