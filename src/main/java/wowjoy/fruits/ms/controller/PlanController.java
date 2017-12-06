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
import wowjoy.fruits.ms.util.DateUtils;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

/**
 * Created by wangziwen on 2017/9/11.
 */
@RestController
@RequestMapping("/v1/plan")
@Transactional
public class PlanController {


    @Qualifier("planDaoImpl")
    @Autowired
    private AbstractDaoPlan dataPlanDao;

    /**
     * @api {get} /v1/plan/{year} 查询某年的年周对照表
     * @apiVersion 0.1.0
     * @apiGroup plan
     */
    @RequestMapping(value = "/year/{year}", method = RequestMethod.GET)
    public RestResult regular(@PathVariable("year") String year) {
        return RestResult.getInstance().setData(DateUtils.getWeekByYear(year));
    }

    /**
     * @api {get} /v1/plan/{uuid} 查询计划详情
     * @apiVersion 0.1.0
     * @apiGroup plan
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.GET)
    public RestResult findByUUID(@PathVariable("uuid") String uuid) {
        FruitPlanVo vo = FruitPlanVo.getVo();
        vo.setUuidVo(uuid);
        return RestResult.getInstance().setData(dataPlanDao.findByUUID(vo));
    }

    /**
     * @api {get} /v1/plan/project/{uuid} 计划综合查询【项目查询】
     * @apiVersion 0.1.0
     * @apiGroup plan
     */
    @RequestMapping(value = "/project", method = RequestMethod.GET)
    public RestResult findMonthWeek(@JsonArgument(type = FruitPlanVo.class) FruitPlanVo fruitPlanVo) {
        return RestResult.getInstance().setData(dataPlanDao.compositeQuery(fruitPlanVo));
    }

    /**
     * @api {post} /v1/plan/project/{uuid} 【项目】计划添加
     * @apiVersion 0.1.0
     * @apiGroup plan
     */
    @RequestMapping(method = RequestMethod.POST)
    public RestResult addJoinProject(@JsonArgument(type = FruitPlanVo.class) FruitPlanVo fruitPlan) {
        dataPlanDao.addJoinProject(fruitPlan);
        return RestResult.getInstance().setData(fruitPlan.getUuid());
    }

    /**
     * @api {put} /v1/plan/{uuid} 计划修改接口
     * @apiVersion 0.1.0
     * @apiGroup plan
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.PUT)
    public RestResult update(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitPlanVo.class) FruitPlanVo fruitPlan) {
        fruitPlan.setUuidVo(uuid);
        dataPlanDao.modify(fruitPlan);
        return RestResult.getInstance().setData(uuid);
    }

    /**
     * @api {put} /v1/plan/complete/{uuid} 修改状态【完成】
     * @apiVersion 0.1.0
     * @apiGroup plan
     */
    @RequestMapping(value = "/complete/{uuid}", method = RequestMethod.PUT)
    public RestResult complete(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitPlanVo.class) FruitPlanVo vo) {
        vo.setUuidVo(uuid);
        dataPlanDao.complete(vo);
        return RestResult.getInstance().setData(uuid);
    }

    /**
     * @api {put} /v1/plan/end/{uuid} 修改状态【终止】
     * @apiVersion 0.1.0
     * @apiGroup plan
     */
    @RequestMapping(value = "/end/{uuid}", method = RequestMethod.PUT)
    public RestResult end(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitPlanVo.class) FruitPlanVo vo) {
        vo.setUuidVo(uuid);
        dataPlanDao.end(vo);
        return RestResult.getInstance().setData(uuid);
    }

    /**
     * @api {put} /v1/plan/{uuid} 删除计划
     * @apiVersion 0.1.0
     * @apiGroup plan
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.DELETE)
    public RestResult delete(@PathVariable("uuid") String uuid) {
        FruitPlanVo vo = FruitPlan.getVo();
        vo.setUuidVo(uuid);
        dataPlanDao.delete(vo);
        return RestResult.getInstance().setData(uuid);
    }

    /**
     * @api {put} /v1/plan/summary/{uuid} 添加进度小结
     * @apiVersion 0.1.0
     * @apiGroup plan
     */
    @RequestMapping(value = "/summary/{uuid}", method = RequestMethod.POST)
    public RestResult insertSummary(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitPlanSummaryVo.class) FruitPlanSummaryVo vo) {
        vo.setPlanId(uuid);
        dataPlanDao.insertSummary(vo);
        return RestResult.getInstance().setData(vo.getUuid());
    }


}
