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
@RequestMapping("/v1/api/task")
public class TaskController {
    @Resource(type = TaskDaoImpl.class)
    private AbstractDaoTask daoTask;

    /**
     * 添加任务-任务关联项目
     * 2017年10月12日12:36:19：
     * 1、实现添加任务
     * 2、实现添加关联项目
     * 3、实现添加执行人
     *
     * @param vo
     * @return
     */
    @RequestMapping(value = "/project", method = RequestMethod.POST)
    public RestResult addJointProject(@JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        daoTask.addJoinProject(vo);
        return RestResult.getInstance().setData(vo.getUuid());
    }

    /**
     * 添加任务-任务关联计划
     * 2017年10月12日12:36:19：
     * 1、实现添加任务
     * 2、实现添加关联计划
     * 3、实现添加执行人
     *
     * @param vo
     * @return
     */
    @RequestMapping(value = "/plan", method = RequestMethod.POST)
    public RestResult addJoinPlan(@JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        daoTask.addJoinPlan(vo);
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
    @RequestMapping(value = "/project/{uuid}", method = RequestMethod.PUT)
    public RestResult modifyJointProject(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        vo.setUuidVo(uuid);
        daoTask.modifyJoinProject(vo);
        return RestResult.getInstance().setData(vo.getUuidVo());
    }

    /**
     * 修改任务-任务关联计划
     * 2017年10月12日13:38:46
     * 1、实现修改任务
     * 2、实现修改关联计划
     * 3、实现添加 and 修改执行人
     *
     * @param vo
     * @return
     */
    @RequestMapping(value = "/plan/{uuid}", method = RequestMethod.PUT)
    public RestResult modifyJoinPlan(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        vo.setUuidVo(uuid);
        daoTask.modifyJoinPlan(vo);
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
     * `
     *
     * @param vo
     * @return
     */
    @RequestMapping(value = "/project", method = RequestMethod.GET)
    public RestResult findJoinProject(@JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        return RestResult.getInstance().setData(daoTask.findJoinProjects(vo));
    }

    /**
     * 查询属于计划的任务列表
     * 1、用户列表提供简单的日期排序
     * 2、提供简单的延期计算功能
     * 2017年10月16日16:20:09-汪梓文：测试通过
     *
     * @param vo
     * @return
     */
    @RequestMapping(value = "/plan", method = RequestMethod.GET)
    public RestResult findJoinPlan(@JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        return RestResult.getInstance().setData(daoTask.findJoinPlans(vo));
    }

}
