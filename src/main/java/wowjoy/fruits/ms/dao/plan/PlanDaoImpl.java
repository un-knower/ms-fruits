package wowjoy.fruits.ms.dao.plan;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.impl.PlanProjectDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.PlanUserDaoImpl;
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

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    @Override
    protected List<FruitPlanDao> findProject(FruitPlanDao dao, Integer pageNum, Integer pageSize, boolean isPage) {
        FruitPlanExample example = this.findTemplate(dao);
        if (StringUtils.isNotBlank(dao.getParentId()))
            example.getOredCriteria().get(0).andParentIdIsNotNull();
        else if (dao.getParentIds() != null && !dao.getParentIds().isEmpty())
            example.getOredCriteria().get(0).andParentIdIn(dao.getParentIds());
        else
            example.getOredCriteria().get(0).andParentIdIsNull();
        if (isPage) PageHelper.startPage(pageNum, pageSize);
        return mapper.selectByProjectId(example, dao.getProjectId());
    }

    @Override
    protected List<FruitPlanDao> findUserByPlanIds(List<String> planIds) {
        if (planIds == null || planIds.isEmpty()) return Lists.newLinkedList();
        return mapper.selectUserByPlanIds(planIds);
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
        example.setOrderByClause("create_date_time desc");
        return mapper.selectByExampleWithBLOBs(example);
    }

    @Override
    protected FruitPlan find(FruitPlanDao dao) {
        FruitPlanExample example = new FruitPlanExample();
        FruitPlanExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        List<FruitPlanDao> datas = mapper.selectByExampleWithBLOBs(example);
        if (datas.isEmpty())
            return FruitPlan.newEmpty("查询计划不存在");
        return datas.get(0);
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
        String sort = sortConstrue(dao);
        if (StringUtils.isNotBlank(sort))
            example.setOrderByClause(sort);
        else
            example.setOrderByClause("create_date_time desc");
        criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name());
        return example;
    }

    private String sortConstrue(FruitPlanDao dao) {
        LinkedList<String> sorts = Lists.newLinkedList();
        if (StringUtils.isNotBlank(dao.getDesc())) {
            for (String desc : dao.getDesc().split(","))
                sorts.add(MessageFormat.format("{0} desc", toMysqlField(desc)));
        }
        if (StringUtils.isNotBlank(dao.getAsc())) {
            for (String asc : dao.getAsc().split(","))
                sorts.add(MessageFormat.format("{0} asc", toMysqlField(asc)));
        }
        if (sorts.isEmpty()) return null;
        return StringUtils.join(sorts, ",");
    }

    private String toMysqlField(String field) {
        String replace = "\\.*[A-Z]";
        Pattern compile = Pattern.compile(replace);
        Matcher matcher = compile.matcher(field);
        StringBuffer result = new StringBuffer();
        Integer lastEnd = 0;
        while (matcher.find()) {
            result.append(field.substring(result.length() > 0 ? result.length() - 1 : 0, matcher.start()) + "_").append(field.substring(matcher.start(), matcher.end()));
            lastEnd = matcher.end();
        }
        result.append(field.substring(lastEnd, field.length()));
        return result.toString();
    }

    @Override
    protected FruitPlan findByUUID(String uuid) {
        if (StringUtils.isBlank(uuid))
            throw new CheckException("【计划】uuId不能为空");
        FruitPlanExample example = new FruitPlanExample();
        example.createCriteria().andUuidEqualTo(uuid);
        List<FruitPlanDao> data = mapper.selectByProjectId(example, null);
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
    public void delete(String uuid) {
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
    public void insertSummary(FruitPlanSummaryDao dao) {
        summaryMapper.insertSelective(dao);
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
        summaryMapper.deleteByExample(example);
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
            planDao.getUserRelation(FruitDict.Systems.ADD).forEach((i) -> {
                userDao.insert(PlanUserRelation.getInstance(planDao.getUuid(), i, FruitDict.PlanUserDict.PRINCIPAL));
            });
            return this;
        }

        public Relation removeUser() {
            planDao.getUserRelation(FruitDict.Systems.DELETE).forEach((i) ->
                    userDao.remove(PlanUserRelation.getInstance(planDao.getUuid(), i, null))
            );
            return this;
        }

        public void removesUser() {
            userDao.remove(PlanUserRelation.getInstance(planDao.getUuid()));
        }

        public void insertProject() {
            planDao.getProjectRelation(FruitDict.Systems.ADD).forEach((i) -> {
                projectdao.insert(PlanProjectRelation.newInstance(planDao.getUuid(), i));
            });
        }

        public Relation removeProject() {
            planDao.getProjectRelation(FruitDict.Systems.DELETE).forEach((i) ->
                    projectdao.remove(PlanProjectRelation.newInstance(planDao.getUuid(), StringUtils.isBlank(i) ? null : i))
            );
            return this;
        }

        public Relation removesProject() {
            projectdao.remove(PlanProjectRelation.newInstance(planDao.getUuid()));
            return this;
        }

    }

}
