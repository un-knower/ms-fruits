package wowjoy.fruits.ms.dao.plan;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.impl.PlanProjectDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.PlanUserDaoImpl;
import wowjoy.fruits.ms.dao.user.UserDaoImpl;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.plan.FruitPlan;
import wowjoy.fruits.ms.module.plan.FruitPlanDao;
import wowjoy.fruits.ms.module.plan.FruitPlanSummary;
import wowjoy.fruits.ms.module.plan.FruitPlanSummaryDao;
import wowjoy.fruits.ms.module.plan.example.FruitPlanExample;
import wowjoy.fruits.ms.module.plan.example.FruitPlanSummaryExample;
import wowjoy.fruits.ms.module.plan.mapper.FruitPlanMapper;
import wowjoy.fruits.ms.module.plan.mapper.FruitPlanSummaryMapper;
import wowjoy.fruits.ms.module.relation.entity.PlanProjectRelation;
import wowjoy.fruits.ms.module.relation.entity.PlanUserRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.List;
import java.util.Objects;

/**
 * Created by wangziwen on 2017/8/25.
 */
@Service
@Transactional
public class PlanDaoImpl extends AbstractDaoPlan {

    @Autowired
    private FruitPlanMapper mapper;
    @Autowired
    private FruitPlanSummaryMapper summaryMapper;
    @Qualifier("planUserDaoImpl")
    @Autowired
    private PlanUserDaoImpl userRelation;
    @Qualifier("planProjectDaoImpl")
    @Autowired
    private PlanProjectDaoImpl projectDao;
    @Autowired
    private UserDaoImpl userDao;


    @Override
    protected List<FruitPlanDao> findProject(FruitPlanDao dao, Integer pageNum, Integer pageSize, boolean isPage) {
        FruitPlanExample example = this.findTemplate(dao);
        if (StringUtils.isNotBlank(dao.getParentId()))
            example.getOredCriteria().get(0).andParentIdIsNotNull();
        else
            example.getOredCriteria().get(0).andParentIdIsNull();
        findTemplate(dao).getOredCriteria().get(0).andParentIdIsNull();
        if (isPage) PageHelper.startPage(pageNum, pageSize);
        return mapper.selectUserByProject(example, dao.getProjectId());
    }

    @Override
    protected List<FruitPlanDao> find(FruitPlanDao dao, Integer pageNum, Integer pageSize) {
        final FruitPlanExample example = this.findTemplate(dao);
        /*查询所有月计划*/
        if (StringUtils.isNotBlank(dao.getParentId()))
            example.getOredCriteria().get(0).andParentIdIsNotNull();
        else
            example.getOredCriteria().get(0).andParentIdIsNull();
        PageHelper.startPage(pageNum, pageSize);
        return mapper.selectByExampleWithBLOBs(example);
    }

    private FruitPlanExample findTemplate(FruitPlanDao dao) {
        final FruitPlanExample example = new FruitPlanExample();
        final FruitPlanExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getTitle()))
            criteria.andTitleEqualTo(dao.getTitle());
        if (StringUtils.isNotBlank(dao.getPlanStatus()))
            criteria.andPlanStatusEqualTo(dao.getPlanStatus());
        if (StringUtils.isNotBlank(dao.getParentId()))
            criteria.andParentIdEqualTo(dao.getParentId());
        if (Objects.nonNull(dao.getStartDateDao()) && Objects.nonNull(dao.getEndDateDao()))
            criteria.andEstimatedEndDateBetween(dao.getStartDateDao(), dao.getEndDateDao());
        if (StringUtils.isNotBlank(dao.getPlanStatus()))
            criteria.andPlanStatusEqualTo(dao.getPlanStatus());
        criteria.andIsDeletedEqualTo(FruitDict.Dict.N.name());
        return example;
    }

    @Override
    protected FruitPlan findByUUID(String uuid) {
        if (StringUtils.isBlank(uuid))
            throw new CheckException("【计划】uuId不能为空");
        FruitPlanExample example = new FruitPlanExample();
        example.createCriteria().andUuidEqualTo(uuid);
        List<FruitPlanDao> data = mapper.selectUserByProject(example, null);
        if (data.isEmpty())
            return FruitPlan.newEmpty("计划不存在");
        return data.get(0);
    }

    /**
     * 关联信息：
     * 1、项目关联
     * 2、责任人关联
     *
     * @param dao
     */
    @Override
    public void insert(FruitPlanDao dao) {
        mapper.insertSelective(dao);
        Relation.getInstance(userRelation, projectDao, dao).insertUser().insertProject();
    }

    @Override
    public void update(FruitPlanDao dao) {
        FruitPlanExample example = new FruitPlanExample();
        FruitPlanExample.Criteria criteria = example.createCriteria();
        criteria.andUuidEqualTo(dao.getUuid());
        mapper.updateByExampleSelective(dao, example);
        Relation.getInstance(userRelation, projectDao, dao)
                .removeUser().removeProject()
                .insertUser().insertProject();
    }

    @Override
    protected void delete(String uuid) {
        FruitPlanExample example = new FruitPlanExample();
        example.createCriteria().andUuidEqualTo(uuid);
        mapper.deleteByExample(example);
        FruitPlanDao plan = FruitPlan.getDao();
        plan.setUuid(uuid);
        Relation.getInstance(userRelation, projectDao, plan).removesProject().removesUser();
        /*删除进度小结*/
        deleteSummarys(FruitPlanSummary.newDao(uuid));
    }

    @Override
    protected void insertSummary(FruitPlanSummaryDao dao) {
        summaryMapper.insertSelective(dao);
    }

    @Override
    protected void deleteSummarys(FruitPlanSummaryDao dao) {
        FruitPlanSummaryExample example = new FruitPlanSummaryExample();
        FruitPlanSummaryExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getPlanId()))
            criteria.andPlanIdEqualTo(dao.getPlanId());
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckException("【删除进度小结】没有可用的删除条件");
        summaryMapper.deleteByExample(example);
    }

    @Override
    protected List<PlanProjectRelation> findJoin(PlanProjectRelation relation) {
        return projectDao.finds(relation);
    }

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

        public Relation insertUser() {
            planDao.getUserRelation(FruitDict.Dict.ADD).forEach((i) -> {
                userDao.insert(PlanUserRelation.getInstance(planDao.getUuid(), i, FruitDict.PlanUserDict.PRINCIPAL));
            });
            return this;
        }

        public Relation removeUser() {
            planDao.getUserRelation(FruitDict.Dict.DELETE).forEach((i) ->
                    userDao.remove(PlanUserRelation.getInstance(planDao.getUuid(), i, null))
            );
            return this;
        }

        public void removesUser() {
            userDao.remove(PlanUserRelation.getInstance(planDao.getUuid()));
        }

        public void insertProject() {
            planDao.getProjectRelation(FruitDict.Dict.ADD).forEach((i) -> {
                projectdao.insert(PlanProjectRelation.newInstance(planDao.getUuid(), i));
            });
        }

        public Relation removeProject() {
            planDao.getProjectRelation(FruitDict.Dict.DELETE).forEach((i) ->
                    projectdao.remove(PlanProjectRelation.newInstance(planDao.getUuid(), i))
            );
            return this;
        }

        public Relation removesProject() {
            projectdao.remove(PlanProjectRelation.newInstance(planDao.getUuid()));
            return this;
        }

    }

}
