package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.dao.plan.AbstractDaoPlan;
import wowjoy.fruits.ms.module.plan.FruitPlan;
import wowjoy.fruits.ms.module.plan.FruitPlanSummaryVo;
import wowjoy.fruits.ms.module.plan.FruitPlanVo;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

/**
 * Created by wangziwen on 2017/9/11.
 */
@RestController
@RequestMapping("/plan")
@Transactional
public class PlanController {


    @Qualifier("planDaoImpl")
    @Autowired
    private AbstractDaoPlan dataPlanDao;

    @RequestMapping(value = "/year/{year}", method = RequestMethod.GET)
    public RestResult regular(@PathVariable("year") String year) {
        return RestResult.getInstance().setData(dataPlanDao.yearEachWeek(Integer.valueOf(year)));
    }

    /**
     * 详情
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.GET)
    public RestResult findByUUID(@PathVariable("uuid") String uuid) {
        FruitPlanVo vo = FruitPlanVo.getVo();
        vo.setUuidVo(uuid);
        return RestResult.getInstance().setData(dataPlanDao.findByUUID(vo));
    }

    /**
     * 月计划列表
     * 分页
     */
    @RequestMapping(value = "/relation/month", method = RequestMethod.GET)
    public RestResult findRelationMonth(@JsonArgument(type = FruitPlanVo.class) FruitPlanVo fruitPlanVo) {
        return RestResult.getInstance().setData(dataPlanDao.findRelationMonth(fruitPlanVo));
    }

    /**
     * 周计划列表
     * 分页
     */
    @RequestMapping(value = "/relation/week", method = RequestMethod.GET)
    public RestResult findRelationWeek(@JsonArgument(type = FruitPlanVo.class) FruitPlanVo fruitPlanVo) {
        return RestResult.getInstance().setData(dataPlanDao.findRelationWeek(fruitPlanVo));
    }

    /**
     * 月计划列表
     * 分页
     */
    @RequestMapping(value = "/month", method = RequestMethod.GET)
    public RestResult findMonth(@JsonArgument(type = FruitPlanVo.class) FruitPlanVo fruitPlanVo) {
        return RestResult.getInstance().setData(dataPlanDao.findMonth(fruitPlanVo));
    }

    /**
     * 周计划列表
     * 分页
     */
    @RequestMapping(value = "/week", method = RequestMethod.GET)
    public RestResult findWeek(@JsonArgument(type = FruitPlanVo.class) FruitPlanVo fruitPlanVo) {
        return RestResult.getInstance().setData(dataPlanDao.findWeek(fruitPlanVo));
    }

    /**
     * 添加
     */
    @RequestMapping(method = RequestMethod.POST)
    public RestResult insert(@JsonArgument(type = FruitPlanVo.class) FruitPlanVo fruitPlan) {
        dataPlanDao.insert(fruitPlan);
        return RestResult.getInstance().setData(fruitPlan.getUuid());
    }

    /**
     * 修改
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.PUT)
    public RestResult findByUUID(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitPlanVo.class) FruitPlanVo fruitPlan) {
        fruitPlan.setUuidVo(uuid);
        dataPlanDao.update(fruitPlan);
        return RestResult.getInstance().setData(uuid);
    }

    /**
     * 终止计划
     *
     * @param uuid
     * @param vo
     * @return
     */
    @RequestMapping(value = "/end/{uuid}", method = RequestMethod.DELETE)
    public RestResult end(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitPlanVo.class) FruitPlanVo vo) {
        vo.setUuidVo(uuid);
        dataPlanDao.end(vo);
        return RestResult.getInstance().setData(uuid);
    }

    /**
     * 删除计划
     *
     * @param uuid
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.DELETE)
    public RestResult delete(@PathVariable("uuid") String uuid) {
        FruitPlanVo vo = FruitPlan.getVo();
        vo.setUuidVo(uuid);
        dataPlanDao.delete(vo);
        return RestResult.getInstance().setData(uuid);
    }

    /**
     * 添加进度小结
     *
     * @param vo
     * @return
     */
    @RequestMapping(value = "/summary", method = RequestMethod.POST)
    public RestResult insertSummary(@JsonArgument(type = FruitPlanSummaryVo.class) FruitPlanSummaryVo vo) {
        dataPlanDao.insertSummary(vo);
        return RestResult.getInstance().setData(vo.getUuid());
    }


}
