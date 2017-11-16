package wowjoy.fruits.ms.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.dao.task.AbstractDaoTask;
import wowjoy.fruits.ms.dao.task.TaskDaoImpl;
import wowjoy.fruits.ms.module.task.FruitTask;
import wowjoy.fruits.ms.module.task.FruitTaskVo;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

import javax.annotation.Resource;

/**
 * Created by wangziwen on 2017/8/30.
 */
@RestController
@RequestMapping("/v1/task")
public class TaskController {
    @Resource(type = TaskDaoImpl.class)
    private AbstractDaoTask daoTask;

    /**
     * 添加任务
     * 实现自动筛选符合条件的数据
     * 1、筛选项目关联
     * 2、筛选任务关联
     *
     * @param vo
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public RestResult insert(@JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        daoTask.insert(vo);
        return RestResult.getInstance().setData(vo.getUuid());
    }

    /**
     * 修改任务-任务关联项目
     * 2017年10月12日13:37:23
     * 1、实现修改任务
     * 2、实现修改关联项目
     * 3、实现添加 and 修改执行人
     *
     * @param vo
     * @return
     */
    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public RestResult modify(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        vo.setUuidVo(uuid);
        daoTask.modify(vo);
        return RestResult.getInstance().setData(vo.getUuidVo());
    }

    /**
     * 修改任务-改变任务状态为已结束
     * 2017年10月12日16:00:12
     * 1、完成修改任务状态至已结束
     *
     * @param uuid
     * @param vo
     * @return
     */
    @RequestMapping(value = "/end/{uuid}", method = RequestMethod.PUT)
    public RestResult changeStatusToEnd(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        vo.setUuidVo(uuid);
        daoTask.changeStatusToEnd(vo);
        return RestResult.getInstance().setData(vo.getUuidVo());
    }

    /**
     * 修改任务-改变任务状态为开始
     * 2017年10月12日17:32:05
     * 1、完成修改任务状态至已开始
     *
     * @param uuid
     * @param vo
     * @return
     */
    @RequestMapping(value = "/start/{uuid}", method = RequestMethod.PUT)
    public RestResult changeStatusToStart(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        vo.setUuidVo(uuid);
        daoTask.changeStatusToStart(vo);
        return RestResult.getInstance().setData(vo.getUuidVo());
    }

    /**
     * 修改任务-改变任务所在列表
     *
     * @param uuid
     * @param vo
     * @return
     */
    @RequestMapping(value = "/list/{uuid}", method = RequestMethod.PUT)
    public RestResult changeList(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        vo.setUuidVo(uuid);
        daoTask.changeList(vo);
        return RestResult.getInstance().setData(vo.getUuidVo());
    }

    /**
     * 删除任务
     * 2017年10月12日15:58:49
     * 1、完成删除任务
     * 2、完成删除关联用户
     * 3、完成删除关联计划
     * 4、完成删除关联项目
     *
     * @param uuid
     * @return
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.DELETE)
    public RestResult delete(@PathVariable("uuid") String uuid) {
        FruitTaskVo vo = FruitTask.getVo();
        vo.setUuidVo(uuid);
        daoTask.delete(vo);
        return RestResult.getInstance().setData(vo.getUuidVo());
    }


    /**
     * 查询属于项目的任务列表
     * 1、用户列表提供简单的日期排序
     * 2、提供简单的延期计算功能
     * 2017年10月16日16:19:46-汪梓文：测试通过
     *
     * @return
     */
    @RequestMapping(value = "/project/{uuid}", method = RequestMethod.GET)
    public RestResult findJoinProject(@PathVariable("uuid") String uuid) {
        FruitTaskVo vo = FruitTask.getVo();
        vo.setProjectIds(uuid);
        return RestResult.getInstance().setData(daoTask.findJoinProjects(vo));
    }

    /**
     * 查询属于项目的任务列表
     * 1、用户列表提供简单的日期排序
     * 2、提供简单的延期计算功能
     * 2017年10月16日16:19:46-汪梓文：测试通过
     *
     * @return
     */
    @RequestMapping(value = "/list/{uuid}", method = RequestMethod.GET)
    public RestResult findByListId(@PathVariable("uuid") String uuid) {
        FruitTaskVo vo = FruitTask.getVo();
        vo.setListId(uuid);
        return RestResult.getInstance().setData(daoTask.findByListId(vo));
    }

    @RequestMapping(value = "/plan/{uuid}", method = RequestMethod.GET)
    public RestResult findByPlan(@PathVariable("uuid") String uuid) {
        FruitTaskVo vo = FruitTask.getVo();
        vo.setPlanId(uuid);
        return RestResult.getInstance().setData(daoTask.findByPlanId(vo));
    }

    /************************************************************************************************
     *                                       个人中心专供                                            *
     ************************************************************************************************/
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public RestResult userFinds(@JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        return RestResult.getInstance().setData(daoTask.userFindByVo(vo));
    }

}
