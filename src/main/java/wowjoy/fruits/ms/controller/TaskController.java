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
     * @api {post} /v1/task 添加任务
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @RequestMapping(method = RequestMethod.POST)
    public RestResult insert(@JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        daoTask.insert(vo);
        return RestResult.getInstance().setData(vo.getUuid());
    }

    /**
     * @api {put} /v1/task/{uuid} 修改任务
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public RestResult modify(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        vo.setUuidVo(uuid);
        daoTask.modify(vo);
        return RestResult.getInstance().setData(vo.getUuidVo());
    }

    /**
     * @api {put} /v1/task/end/{uuid} 变更任务状态【结束】
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @RequestMapping(value = "/end/{uuid}", method = RequestMethod.PUT)
    public RestResult changeStatusToEnd(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        vo.setUuidVo(uuid);
        daoTask.changeStatusToEnd(vo);
        return RestResult.getInstance().setData(vo.getUuidVo());
    }

    /**
     * @api {put} /v1/task/start/{uuid} 变更任务状态【开始】
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @RequestMapping(value = "/start/{uuid}", method = RequestMethod.PUT)
    public RestResult changeStatusToStart(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        vo.setUuidVo(uuid);
        daoTask.changeStatusToStart(vo);
        return RestResult.getInstance().setData(vo.getUuidVo());
    }

    /**
     * @api {put} /v1/task/list/{uuid} 改变当前任务所在列表
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @RequestMapping(value = "/list/{uuid}", method = RequestMethod.PUT)
    public RestResult changeList(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        vo.setUuidVo(uuid);
        daoTask.changeList(vo);
        return RestResult.getInstance().setData(vo.getUuidVo());
    }

    /**
     * @api {delete} /v1/task/{uuid} 删除任务
     * @apiVersion 0.1.0
     * @apiGroup task
     * @apiDescription 1、完成删除任务
     * 2、完成删除关联用户
     * 3、完成删除关联计划
     * 4、完成删除关联项目
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.DELETE)
    public RestResult delete(@PathVariable("uuid") String uuid) {
        FruitTaskVo vo = FruitTask.getVo();
        vo.setUuidVo(uuid);
        daoTask.delete(vo);
        return RestResult.getInstance().setData(vo.getUuidVo());
    }


    /**
     * @api {get} /v1/task/project/{uuid} 根据指定项目id查询对应的任务列表
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @RequestMapping(value = "/project/{uuid}", method = RequestMethod.GET)
    public RestResult findJoinProject(@PathVariable("uuid") String uuid) {
        FruitTaskVo vo = FruitTask.getVo();
        vo.setProjectIds(uuid);
        return RestResult.getInstance().setData(daoTask.findJoinProjects(vo));
    }

    /**
     * @api {get} /v1/task/list/{uuid} 根据指定列表id，查询对应的任务列表下所有任务
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @RequestMapping(value = "/list/{uuid}", method = RequestMethod.GET)
    public RestResult findByListId(@PathVariable("uuid") String uuid) {
        FruitTaskVo vo = FruitTask.getVo();
        vo.setListId(uuid);
        return RestResult.getInstance().setData(daoTask.findByListId(vo));
    }

    /**
     * @api {get} /v1/task/plan/{uuid} 根据指定的计划id，查询计划对应的任务
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @RequestMapping(value = "/plan/{uuid}", method = RequestMethod.GET)
    public RestResult findByPlan(@PathVariable("uuid") String uuid) {
        FruitTaskVo vo = FruitTask.getVo();
        vo.setPlanId(uuid);
        return RestResult.getInstance().setData(daoTask.findByPlanId(vo));
    }

    /************************************************************************************************
     *                                       个人中心专供                                            *
     ************************************************************************************************/

    /**
     * @api {get} /v1/task/user 查询当前登录用户的所有任务列表
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public RestResult userFinds(@JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        return RestResult.getInstance().setData(daoTask.userFindByVo(vo));
    }

}
