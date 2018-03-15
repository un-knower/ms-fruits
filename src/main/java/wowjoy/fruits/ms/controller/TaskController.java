package wowjoy.fruits.ms.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.aspectj.LogInfo;
import wowjoy.fruits.ms.dao.task.AbstractDaoTask;
import wowjoy.fruits.ms.dao.task.TaskDaoImpl;
import wowjoy.fruits.ms.module.task.FruitTask;
import wowjoy.fruits.ms.module.task.FruitTaskVo;
import wowjoy.fruits.ms.module.task.FruitTaskVo.TaskTransferVo;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

import javax.annotation.Resource;
import java.util.function.Predicate;

import static wowjoy.fruits.ms.util.RestResult.getInstance;

@RestController
@RequestMapping("/v1/task")
public class TaskController {
    @Resource(type = TaskDaoImpl.class)
    private AbstractDaoTask daoTask;

    /**
     * @api {post} /v1/task 添加任务
     * @apiVersion 0.1.0
     * @apiGroup task
     * @apiParamExample {json} 关联计划:
     * {
     * "description":"2017年11月15日10:35:55：测试任务添加-计划",
     * "estimatedEndDate":"2017-11-15",
     * "title":"测试任务添加-计划",
     * "taskLevel":"LOW",
     * "userRelation":{
     * "ADD":[{
     * "userId":"fbdebd622b75404a9258e6ddd0c13a79"
     * }]
     * },
     * "listRelation":{
     * "ADD":[{
     * "listId":"6c59f8d69a27406c835f7a8f0d44a71f"
     * }]
     * },"planRelation":{
     * "ADD":[{
     * "planId":"963b729b7677406bbc3aa7eac2f58b19"
     * }]
     * }
     * }
     * @apiParamExample {json} 关联项目:
     * {
     * "description":"2017年11月15日10:35:55：测试任务添加-计划",
     * "estimatedEndDate":"2017-11-15",
     * "title":"测试任务添加-计划",
     * "taskLevel":"LOW",
     * "userRelation":{
     * "ADD":[{
     * "userId":"fbdebd622b75404a9258e6ddd0c13a79"
     * }]
     * },
     * "listRelation":{
     * "ADD":[{
     * "listId":"6c59f8d69a27406c835f7a8f0d44a71f"
     * }]
     * },"projectRelation":{
     * "ADD":[{
     * "projectId":"5db11c2ee68e49208c368a9a670a7bbb"
     * }]
     * }
     * }
     */
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.TASK, operateType = FruitDict.LogsDict.ADD)
    @RequestMapping(method = RequestMethod.POST)
    public RestResult insert(@JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        daoTask.insert(vo);
        return getInstance().setData(vo.getUuid());
    }

    /**
     * @api {put} /v1/task/{uuid} 修改任务
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @LogInfo(uuid = "uuidVo", type = FruitDict.Parents.TASK, operateType = FruitDict.LogsDict.UPDATE)
    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public RestResult modify(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        vo.setUuidVo(uuid);
        /*若变更记录中包含人员变更，将日志状态改为 STAFF_CHANGE*/
        vo.setOperateTypeSupplier(() -> {
            Predicate<FruitDict.Systems> userPredicate = (o) -> vo.getUserRelation().containsKey(o) && !vo.getUserRelation().get(o).isEmpty();
            if (vo.getUserRelation() != null && (userPredicate.test(FruitDict.Systems.ADD) || userPredicate.test(FruitDict.Systems.DELETE)))
                return FruitDict.LogsDict.STAFF_CHANGE;
            return FruitDict.LogsDict.UPDATE;
        });
        daoTask.modify(vo);
        return getInstance().setData(vo.getUuidVo());
    }

    /**
     * @api {put} /v1/task/complete/{uuid} 变更任务状态【完成】
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.TASK, operateType = FruitDict.LogsDict.COMPLETE)
    @RequestMapping(value = "/complete/{uuid}", method = RequestMethod.PUT)
    public RestResult changeStatusToComplete(@PathVariable("uuid") String uuid) {
        FruitTaskVo vo = FruitTask.getVo();
        vo.setUuidVo(uuid);
        daoTask.changeStatusToComplete(vo);
        return getInstance().setData(vo.getUuidVo());
    }

    /**
     * @api {put} /v1/task/start/{uuid} 变更任务状态【开始】
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.TASK, operateType = FruitDict.LogsDict.START)
    @RequestMapping(value = "/start/{uuid}", method = RequestMethod.PUT)
    public RestResult changeStatusToStart(@PathVariable("uuid") String uuid) {
        FruitTaskVo vo = FruitTask.getVo();
        vo.setUuidVo(uuid);
        daoTask.changeStatusToStart(vo);
        return getInstance().setData(vo.getUuidVo());
    }

    /**
     * @api {put} /v1/task/end/{uuid} 变更任务状态【终止】
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @LogInfo(uuid = "uuidVo", type = FruitDict.Parents.TASK, operateType = FruitDict.LogsDict.END)
    @RequestMapping(value = "/end/{uuid}", method = RequestMethod.PUT)
    public RestResult changeStatusToEnd(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        vo.setUuidVo(uuid);
        daoTask.changeStatusToEnd(vo);
        return getInstance().setData(vo.getUuidVo());
    }

    /**
     * @api {put} /v1/task/list/{uuid} 改变当前任务所在列表
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @LogInfo(uuid = "uuidVo", type = FruitDict.Parents.TASK, operateType = FruitDict.LogsDict.MOVE_TASK)
    @RequestMapping(value = "/list/{uuid}", method = RequestMethod.PUT)
    public RestResult changeList(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        vo.setUuidVo(uuid);
        daoTask.changeList(vo);
        return getInstance().setData(vo.getUuidVo());
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
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.TASK, operateType = FruitDict.LogsDict.DELETE)
    @RequestMapping(value = "{uuid}", method = RequestMethod.DELETE)
    public RestResult delete(@PathVariable("uuid") String uuid) {
        FruitTaskVo vo = FruitTask.getVo();
        vo.setUuidVo(uuid);
        daoTask.delete(vo);
        return getInstance().setData(vo.getUuidVo());
    }


    /**
     * @api {get} /v1/task/project/{uuid} 根据指定项目id查询对应的任务列表
     * @apiVersion 0.1.0
     * @apiGroup task
     * @apiParam {String} listTitle 列表名称查询，支持前后模糊
     * @apiParam {String} title 任务名称查询，支持前后模糊
     */
    @RequestMapping(value = "/project/{uuid}", method = RequestMethod.GET)
    public RestResult findJoinProject(@PathVariable("uuid") String uuid,
                                      @JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        return getInstance().setData(daoTask.findJoinProjects(uuid, vo));
    }

    /**
     * @api {get} /v1/task/{uuid} 根据任务UUID查询任务详情
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.GET)
    public RestResult findTaskInfo(@PathVariable("uuid") String uuid) {
        return getInstance().setData(daoTask.findTaskInfo(uuid));
    }

    /**
     * @api {put} /v1/task/transfer/{uuid} 任务转交
     * @apiVersion 2.5.0
     * @apiGroup task
     * @apiParamExample {json} 转交栗子：
     * {
     * "reason":"神兽保佑",
     * "transferUser":[{"userId":"8401e45249434eafb7654447e02397a2"},{"userId":"d9f6e08b897247b7a02fb8ff6b8fc558"}]
     * }
     */
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.TASK, operateType = FruitDict.LogsDict.TRANSFER)
    @RequestMapping(value = "transfer/{uuid}", method = RequestMethod.PUT)
    public RestResult transfer(@PathVariable("uuid") String uuid, @JsonArgument(type = TaskTransferVo.class) TaskTransferVo transferVo) {
        transferVo.setUuidVo(uuid);
        daoTask.transfer(transferVo);
        return getInstance().setData(uuid);
    }

    /************************************************************************************************
     *                                       个人中心专供                                            *
     ************************************************************************************************/

    /**
     * @api {get} /v1/task/current 查询当前登录用户的所有任务列表
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public RestResult myTask(@JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        return getInstance().setData(daoTask.myTask(vo));
    }

    /**
     * @api {get} /v1/task/currente/end 查询当前登录用户，所有已完成的任务列表
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @RequestMapping(value = "/current/end", method = RequestMethod.GET)
    public RestResult myTaskByEnd(@JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        return getInstance().setData(daoTask.myTaskByEnd(vo));
    }

    /**
     * @api {get} /v1/task/current_create 查询当前登录用户创建的任务
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @RequestMapping(value = "/current_create", method = RequestMethod.GET)
    public RestResult userFinds(@JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        return getInstance().setData(daoTask.myCreateTask(vo));
    }

}
