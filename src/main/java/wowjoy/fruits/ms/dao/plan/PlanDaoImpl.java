package wowjoy.fruits.ms.dao.plan;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.impl.PlanUserDaoImpl;
import wowjoy.fruits.ms.module.plan.FruitPlan;
import wowjoy.fruits.ms.module.plan.FruitPlanDao;
import wowjoy.fruits.ms.module.plan.example.FruitPlanExample;
import wowjoy.fruits.ms.module.plan.mapper.FruitPlanMapper;
import wowjoy.fruits.ms.module.relation.entity.PlanUserRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.text.MessageFormat;
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
    @Qualifier("planUserDaoImpl")
    @Autowired
    private PlanUserDaoImpl userDao;


    @Override
    protected List<FruitPlanDao> findRelationMonth(FruitPlanDao dao, Integer pageNum, Integer pageSize) {
        final FruitPlanExample example = new FruitPlanExample();
        final FruitPlanExample.Criteria criteria = example.createCriteria();
        /*查询所有月计划*/
        criteria.andParentIdIsNull();
        if (StringUtils.isNotBlank(dao.getTitle()))
            criteria.andTitleEqualTo(dao.getTitle());
        if (StringUtils.isNotBlank(dao.getPlanStatus()))
            criteria.andPlanStatusEqualTo(dao.getPlanStatus());
        if (Objects.nonNull(dao.getStartDateDao()) && Objects.nonNull(dao.getEndDateDao()))
            criteria.andEndDateBetween(dao.getStartDateDao(), dao.getEndDateDao());
        PageHelper.startPage(pageNum, pageSize);
        return mapper.selectUserByExampleWithBLOBs(example);
    }

    @Override
    protected List<FruitPlanDao> findRelationWeek(FruitPlanDao dao, Integer pageNum, Integer pageSize) {
        final FruitPlanExample example = new FruitPlanExample();
        final FruitPlanExample.Criteria criteria = example.createCriteria();
        /*查询所有月计划*/
        criteria.andParentIdIsNull();
        ;
        if (StringUtils.isNotBlank(dao.getTitle()))
            criteria.andTitleEqualTo(dao.getTitle());
        if (StringUtils.isNotBlank(dao.getPlanStatus()))
            criteria.andPlanStatusEqualTo(dao.getPlanStatus());
        if (Objects.nonNull(dao.getStartDateDao()) && Objects.nonNull(dao.getEndDateDao()))
            criteria.andEndDateBetween(dao.getStartDateDao(), dao.getEndDateDao());
        PageHelper.startPage(pageNum, pageSize);
        return mapper.selectUserByExampleWithBLOBs(example);
    }

    @Override
    protected List<FruitPlanDao> findMonth(FruitPlanDao dao, Integer pageNum, Integer pageSize) {
        final FruitPlanExample example = new FruitPlanExample();
        final FruitPlanExample.Criteria criteria = example.createCriteria();
        /*查询所有月计划*/
        criteria.andParentIdIsNull();
        if (StringUtils.isNotBlank(dao.getTitle()))
            criteria.andTitleLike(MessageFormat.format("%{0}%", dao.getTitle()));
        if (StringUtils.isNotBlank(dao.getPlanStatus()))
            criteria.andPlanStatusEqualTo(dao.getPlanStatus());
        if (Objects.nonNull(dao.getStartDateDao()) && Objects.nonNull(dao.getEndDateDao()))
            criteria.andEndDateBetween(dao.getStartDateDao(), dao.getEndDateDao());
        PageHelper.startPage(pageNum, pageSize);
        return mapper.selectByExampleWithBLOBs(example);
    }

    @Override
    protected List<FruitPlanDao> findWeek(FruitPlanDao dao, Integer pageNum, Integer pageSize) {
        final FruitPlanExample example = new FruitPlanExample();
        final FruitPlanExample.Criteria criteria = example.createCriteria();
        /*查询所有月计划*/
        criteria.andParentIdIsNull();
        if (StringUtils.isNotBlank(dao.getTitle()))
            criteria.andTitleEqualTo(dao.getTitle());
        if (StringUtils.isNotBlank(dao.getPlanStatus()))
            criteria.andPlanStatusEqualTo(dao.getPlanStatus());
        if (Objects.nonNull(dao.getStartDateDao()) && Objects.nonNull(dao.getEndDateDao()))
            criteria.andEndDateBetween(dao.getStartDateDao(), dao.getEndDateDao());
        PageHelper.startPage(pageNum, pageSize);
        return mapper.selectByExampleWithBLOBs(example);
    }

    @Override
    protected FruitPlan findByUUID(String uuid) {
        if (StringUtils.isBlank(uuid))
            throw new CheckPlanException("【计划】uuId不能为空");
        FruitPlanExample example = new FruitPlanExample();
        example.createCriteria().andUuidEqualTo(uuid);
        List<FruitPlanDao> data = mapper.selectUserByExampleWithBLOBs(example);
        if (data.isEmpty())
            return FruitPlan.newEmpty("计划不存在");
        return data.get(0);
    }

    @Override
    public void insert(FruitPlanDao dao) {
        mapper.insertSelective(dao);
        Relation.getInstance(userDao, dao).insertPlanUser();
    }

    @Override
    public void update(FruitPlanDao dao) {
        if (StringUtils.isBlank(dao.getUuid()))
            throw new CheckPlanException("【计划】uuId不能为空");
        FruitPlanExample example = new FruitPlanExample();
        FruitPlanExample.Criteria criteria = example.createCriteria();
        criteria.andUuidEqualTo(dao.getUuid());
        mapper.updateByExampleSelective(dao, example);
        Relation.getInstance(userDao, dao).removePlanUser().insertPlanUser();
    }

    private static class Relation {
        private final PlanUserDaoImpl userDao;
        private final FruitPlanDao planDao;


        private Relation(PlanUserDaoImpl userDao, FruitPlanDao planDao) {
            this.userDao = userDao;
            this.planDao = planDao;
        }

        public static Relation getInstance(PlanUserDaoImpl userDao, FruitPlanDao planDao) {
            return new Relation(userDao, planDao);
        }

        public void insertPlanUser() {
            planDao.getUserRelation().forEach((i) -> {
                i.setPuRole(FruitDict.UserProjectDict.PRINCIPAL.name());
                i.setPlanId(planDao.getUuid());
                userDao.insert(i);
            });
        }

        public Relation removePlanUser() {
            userDao.remove(PlanUserRelation.getInstance(planDao.getUuid(), null));
            return this;
        }

    }

}
