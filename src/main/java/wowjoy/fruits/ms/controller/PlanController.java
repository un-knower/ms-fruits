package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.aspectj.LogInfo;
import wowjoy.fruits.ms.dao.plan.AbstractDaoPlan;
import wowjoy.fruits.ms.module.plan.FruitPlan;
import wowjoy.fruits.ms.module.plan.FruitPlanVo;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
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


    private final AbstractDaoPlan dataPlanDao;

    @Autowired
    public PlanController(@Qualifier("planDaoImpl") AbstractDaoPlan dataPlanDao) {
        this.dataPlanDao = dataPlanDao;
    }

    /**
     * @api {get} /v1/plan/week/{year} 每周的开始和结束时间
     * @apiVersion 0.1.0
     * @apiGroup plan
     */
    @RequestMapping(value = "/week/{year}", method = RequestMethod.GET)
    public RestResult yearWeek(@PathVariable("year") String year) {
        return RestResult.newSuccess().setData(DateUtils.getWeekByYear(year));
    }

    /**
     * @api {get} /v1/plan/month/{year} 每月的开始和结束时间
     * @apiVersion 0.1.0
     * @apiGroup plan
     */
    @RequestMapping(value = "/month/{year}", method = RequestMethod.GET)
    public RestResult yearMonth(@PathVariable("year") String year) {
        return RestResult.newSuccess().setData(DateUtils.getMonthBetween(year));
    }

    /**
     * @api {get} /v1/plan/{planId} 目标详情
     * @apiVersion 0.1.0
     * @apiGroup plan
     * @apiParam {String} planId 目标uuid
     * @apiSuccess (1000) {String} logs 操作日志
     * @apiSuccess (1000) {String} tasks 任务集合
     * @apiSuccess (1000) {String} users 用户集合
     * @apiSuccess (1000) {String} title 标题
     * @apiSuccess (1000) {String} description 描述
     * @apiSuccess (1000) {String} planStatus 目标状态
     * @apiSuccess (1000) {String} endDate 实际结束时间
     * @apiSuccess (1000) {String} startDate 实际开始时间
     * @apiSuccess (1000) {String} estimatedStartDate 预计开始时间
     * @apiSuccess (1000) {String} estimatedEndDate 预计结束时间
     */
    @RequestMapping(value = "{planId}", method = RequestMethod.GET)
    public RestResult findInfo(@PathVariable("planId") String uuid) {
        return RestResult.newSuccess().setData(ApiDataFactory.PlanController.findInfo.apply(dataPlanDao.findInfo(uuid).toInfo()));
    }

    /**
     * @api {get} /v1/plan/project/composite/{projectId} 项目-目标综合查询
     * @apiVersion 2.5.0
     * @apiGroup plan
     * @apiParam {String} projectId 项目uuid
     * @apiParam title String 根据目标名称查询，前后模糊查询
     * @apiParam startDate String 时间范围查询，开始时间
     * @apiParam endDate String 时间范围查询，结束时间
     * @apiParam planStatus String 时间范围查询，目标状态查询，支持多状态查询
     * @apiParam year String 年份（会导致自定义startDate、endDate失效）
     * @apiParam month String 年份（会导致自定义startDate、endDate失效）
     * @apiSuccess (1000) {String} plans 目标列表
     * @apiSuccess (1000) {json} dataCount 目标状态统计数据
     * @apiSuccessExample {json} plans 内部参数
     * @apiSuccessExample {json} dataCount内部参数
     * {
     * "DELAY_COMPLETE": 2, //延期完成
     * "DELAY": 1,  //延期
     * "COMPLETE": 2,   //按时完成
     * "END": 2 //终止
     * }
     */
    @RequestMapping(value = "/project/composite/{uuid}", method = RequestMethod.GET)
    public RestResult findComposite(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitPlan.Query.class) FruitPlan.Query vo) {
        vo.setProjectId(uuid);
        return RestResult.newSuccess().setData(ApiDataFactory.PlanController.findComposite.apply(dataPlanDao.compositeQuery(vo)));
    }

    /**
     * @api {get} /v1/plan/project/{projectId} 项目-目标查询
     * @apiVersion 0.1.0
     * @apiGroup plan
     * @apiParam {String} projectId 项目uuid
     * @apiParam title String 根据目标名称查询，前后模糊查询
     * @apiParam planStatus String 状态查询，支持多查询。多个以逗号隔开：STAY_PENDING,PENDING,COMPLETE,COMPLETE
     * @apiParam startDate String 时间范围查询，开始时间
     * @apiParam endDate String 时间范围查询，结束时间
     * @apiParam year String 年份（会导致自定义startDate、endDate失效）
     * @apiParam month String 年份（会导致自定义startDate、endDate失效）
     */
    @RequestMapping(value = "/project/{projectId}", method = RequestMethod.GET)
    public RestResult find(@PathVariable("projectId") String uuid, @JsonArgument(type = FruitPlan.Query.class) FruitPlan.Query query) {
        query.setProjectId(uuid);
        return RestResult.newSuccess().setData(ApiDataFactory.PlanController.find.apply(dataPlanDao.findList(query)));
    }

    /**
     * @api {post} /v1/plan 项目-目标添加
     * @apiVersion 0.1.0
     * @apiGroup plan
     * @apiParam title String 标题
     * @apiParam description String 描述
     * @apiParam estimatedStartDate String 预计开始时间
     * @apiParam estimatedEndDate String 预计结束时间
     * @apiParam userRelation json 关联用户
     * @apiParam userRelation json 所属项目
     * @apiParamExample {json} userRelation:
     * {"ADD":["8401e45249434eafb7654447e02397a2"]}
     * @apiParamExample {json} projectRelation:
     * {"ADD":["e41e0c03ee704b31b56f2ec1076609b5"]}
     */
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.PLAN, operateType = FruitDict.LogsDict.ADD)
    @RequestMapping(method = RequestMethod.POST)
    public RestResult addJoinProject(@JsonArgument(type = FruitPlan.Insert.class) FruitPlan.Insert insert) {
        dataPlanDao.addJoinProject(insert);
        return RestResult.newSuccess().setData(insert.getUuid());
    }

    /**
     * @api {put} /v1/plan/{uuid} 目标修改
     * @apiVersion 0.1.0
     * @apiGroup plan
     * @apiParam {String} uuid 目标uuid
     */
    @LogInfo(uuid = "uuidVo", type = FruitDict.Parents.PLAN, operateType = FruitDict.LogsDict.UPDATE)
    @RequestMapping(value = "{uuid}", method = RequestMethod.PUT)
    public RestResult update(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitPlanVo.class) FruitPlanVo vo) {
        vo.setUuidVo(uuid);
        dataPlanDao.modify(vo);
        return RestResult.newSuccess().setData(uuid);
    }

    /**
     * @api {put} /v1/plan/complete/{uuid} 修改状态【完成】
     * @apiVersion 0.1.0
     * @apiGroup plan
     * @apiParam {String} uuid 目标uuid
     * @apiParam startDate Date 待执行直接跳转到已完成，需要填写实际开始时间
     * @apiParam statusDescription String 若目标延期，需要填写延期说明
     * @apiParam endDate Date 若目标延期，可选择更新实际结束时间，不能大于当前。若不填写，默认当前时间
     */
    @LogInfo(uuid = "uuidVo", type = FruitDict.Parents.PLAN, operateType = FruitDict.LogsDict.COMPLETE)
    @RequestMapping(value = "/complete/{uuid}", method = RequestMethod.PUT)
    public RestResult complete(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitPlanVo.class) FruitPlanVo vo) {
        vo.setUuidVo(uuid);
        dataPlanDao.complete(vo);
        return RestResult.newSuccess().setData(uuid);
    }

    /**
     * @api {put} /v1/plan/end/{uuid} 修改状态【终止】
     * @apiVersion 0.1.0
     * @apiGroup plan
     * @apiParam statusDescription String 终止理由，后台为限制必填，前端根据产品需求决定
     */
    @LogInfo(uuid = "uuidVo", type = FruitDict.Parents.PLAN, operateType = FruitDict.LogsDict.END)
    @RequestMapping(value = "/end/{uuid}", method = RequestMethod.PUT)
    public RestResult end(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitPlanVo.class) FruitPlanVo vo) {
        vo.setUuidVo(uuid);
        dataPlanDao.end(vo);
        return RestResult.newSuccess().setData(uuid);
    }

    /**
     * @api {put} /v1/plan/pending/{uuid} 修改状态【进行中】
     * @apiVersion 0.1.0
     * @apiGroup plan
     * @apiDescription 只能将待执行状态切换至进行中
     */
    @LogInfo(uuid = "uuidVo", type = FruitDict.Parents.PLAN, operateType = FruitDict.LogsDict.PENDING)
    @RequestMapping(value = "/pending/{uuid}", method = RequestMethod.PUT)
    public RestResult pending(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitPlanVo.class) FruitPlanVo vo) {
        vo.setUuidVo(uuid);
        dataPlanDao.pending(vo);
        return RestResult.newSuccess().setData(uuid);
    }

    /**
     * @api {put} /v1/plan/{uuid} 删除目标
     * @apiVersion 0.1.0
     * @apiGroup plan
     */
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.PLAN, operateType = FruitDict.LogsDict.DELETE)
    @RequestMapping(value = "{uuid}", method = RequestMethod.DELETE)
    public RestResult delete(@PathVariable("uuid") String uuid) {
        FruitPlanVo vo = FruitPlan.getVo();
        vo.setUuidVo(uuid);
        dataPlanDao.delete(vo);
        return RestResult.newSuccess().setData(uuid);
    }

}
