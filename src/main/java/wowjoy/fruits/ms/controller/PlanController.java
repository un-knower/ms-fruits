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
@RequestMapping("/v1/api/plan")
@Transactional
public class PlanController {


    @Qualifier("planDaoImpl")
    @Autowired
    private AbstractDaoPlan dataPlanDao;

    /**
     * 查询某年的星期位置
     * 2017年10月10日10:04:01：
     * 测试通过
     *
     * @param year
     * @return
     */
    @RequestMapping(value = "/year/{year}", method = RequestMethod.GET)
    public RestResult regular(@PathVariable("year") String year) {
        return RestResult.getInstance().setData(dataPlanDao.yearEachWeek(Integer.valueOf(year)));
    }

    /**
     * 计划详情
     * 2017年10月10日10:04:30：测试通过
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
     * 2017年10月10日10:07:01：测试通过。查询速度230ms左右
     */
    @RequestMapping(value = "/relation/month", method = RequestMethod.GET)
    public RestResult findRelationMonth(@JsonArgument(type = FruitPlanVo.class) FruitPlanVo fruitPlanVo) {
        return RestResult.getInstance().setData(dataPlanDao.findRelationMonth(fruitPlanVo));
    }

    /**
     * 周计划列表
     * 分页
     * 2017年10月10日10:07:41：测试通过。查询速度380ms左右
     */
    @RequestMapping(value = "/relation/week", method = RequestMethod.GET)
    public RestResult findRelationWeek(@JsonArgument(type = FruitPlanVo.class) FruitPlanVo fruitPlanVo) {
        return RestResult.getInstance().setData(dataPlanDao.findRelationWeek(fruitPlanVo));
    }

    /**
     * 月计划列表
     * 分页
     * 2017年10月10日11:49:18：测试通过。查询速度100ms左右
     */
    @RequestMapping(value = "/month", method = RequestMethod.GET)
    public RestResult findMonth(@JsonArgument(type = FruitPlanVo.class) FruitPlanVo fruitPlanVo) {
        return RestResult.getInstance().setData(dataPlanDao.findMonth(fruitPlanVo));
    }

    /**
     * 周计划列表
     * 分页
     * 2017年10月10日11:51:28：修改查询条件，只查询parentid不为空的字段
     * 2017年10月10日11:52:09：测试通过。查询速度100ms左右
     */
    @RequestMapping(value = "/week", method = RequestMethod.GET)
    public RestResult findWeek(@JsonArgument(type = FruitPlanVo.class) FruitPlanVo fruitPlanVo) {
        return RestResult.getInstance().setData(dataPlanDao.findWeek(fruitPlanVo));
    }

    /**
     * 添加
     * 2017年10月10日11:58:32：测试通过。
     */
    @RequestMapping(method = RequestMethod.POST)
    public RestResult insert(@JsonArgument(type = FruitPlanVo.class) FruitPlanVo fruitPlan) {
        dataPlanDao.insert(fruitPlan);
        return RestResult.getInstance().setData(fruitPlan.getUuid());
    }

    /**
     * 修改
     * 贴士：
     * 1、修改接口不包含状态变更，需要变更状态直接调用修改状态接口
     * 2017年10月10日11:58:46：测试通过。
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.PUT)
    public RestResult update(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitPlanVo.class) FruitPlanVo fruitPlan) {
        fruitPlan.setUuidVo(uuid);
        dataPlanDao.modify(fruitPlan);
        return RestResult.getInstance().setData(uuid);
    }

    /**
     * 修改状态-完成计划
     * 2017年10月10日12:01:56：测试通过。已终止或已完成的计划不能修改
     *
     * @param uuid
     * @param vo
     * @return
     */
    @RequestMapping(value = "/complete/{uuid}", method = RequestMethod.PUT)
    public RestResult complete(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitPlanVo.class) FruitPlanVo vo) {
        vo.setUuidVo(uuid);
        dataPlanDao.complete(vo);
        return RestResult.getInstance().setData(uuid);
    }

    /**
     * 修改状态-终止计划
     * 2017年10月10日11:59:00：测试通过
     *
     * @param uuid
     * @param vo
     * @return
     */
    @RequestMapping(value = "/end/{uuid}", method = RequestMethod.PUT)
    public RestResult end(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitPlanVo.class) FruitPlanVo vo) {
        vo.setUuidVo(uuid);
        dataPlanDao.end(vo);
        return RestResult.getInstance().setData(uuid);
    }

    /**
     * 删除计划
     * 2017年10月10日13:52:32：测试通过
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
     * <p>
     * 2017年10月10日14:01:41：添加进度小结
     *
     * @param vo
     * @return
     */
    @RequestMapping(value = "/summary/{uuid}", method = RequestMethod.POST)
    public RestResult insertSummary(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitPlanSummaryVo.class) FruitPlanSummaryVo vo) {
        vo.setPlanId(uuid);
        dataPlanDao.insertSummary(vo);
        return RestResult.getInstance().setData(vo.getUuid());
    }


}
