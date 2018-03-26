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

import static wowjoy.fruits.ms.util.RestResult.newSuccess;

@RestController
@RequestMapping("/v1/task")
public class TaskController {
    @Resource(type = TaskDaoImpl.class)
    private AbstractDaoTask daoTask;

    /**
     * @api {post} /v1/task 添加任务
     * @apiVersion 2.5.0
     * @apiGroup task
     * @apiParam {String} title 标题
     * @apiParam {String} estimatedEndDate 任务预计结束时间
     * @apiParam {String} description 任务描述
     * @apiParam {String} listRelation 所属分类
     * @apiParam {String} planRelation 所属目标
     * @apiParam {String} projectRelation 所属项目（目标优先）
     * @apiParamExample {json} listRelation 所属分类内部参数
     * {"ADD":[{"listId":"6c59f8d69a27406c835f7a8f0d44a71f"}]}
     * @apiParamExample {json} planRelation 所属目标内部参数
     * {"ADD":[{"planId":"fefb171a3a5e44d5aad52d6d6b23af7b"}]}
     * @apiParamExample {json} projectRelation 所属项目内部参数
     * {"ADD":[{"projectId":"e41e0c03ee704b31b56f2ec1076609b5"}]}
     */
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.TASK, operateType = FruitDict.LogsDict.ADD)
    @RequestMapping(method = RequestMethod.POST)
    public RestResult insert(@JsonArgument(type = FruitTask.Insert.class) FruitTask.Insert insert) {
        daoTask.insertBefore(insert);
        return newSuccess().setData(insert.getUuid());
    }

    /**
     * @api {put} /v1/task/{uuid} 修改任务
     * @apiVersion 2.5.0
     * @apiGroup task
     * @apiParam {String} title 标题
     * @apiParam {String} estimatedEndDate 任务预计结束时间
     * @apiParam {String} description 任务描述
     * @apiParam {String} listRelation 所属分类
     * @apiParam {String} planRelation 所属目标
     * @apiParam {String} projectRelation 所属项目（目标优先）
     * @apiParamExample {json} listRelation 所属分类内部参数
     * {"ADD":[{"listId":"6c59f8d69a27406c835f7a8f0d44a71f"}]}
     * @apiParamExample {json} planRelation 所属目标内部参数
     * {"ADD":[{"planId":"fefb171a3a5e44d5aad52d6d6b23af7b"}]}
     * @apiParamExample {json} projectRelation 所属项目内部参数
     * {"ADD":[{"projectId":"e41e0c03ee704b31b56f2ec1076609b5"}]}
     */
    @LogInfo(uuid = "uuidVo", type = FruitDict.Parents.TASK, operateType = FruitDict.LogsDict.UPDATE)
    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public RestResult modify(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitTask.Update.class) FruitTask.Update update) {
        update.setUuid(uuid);
        /*若变更记录中包含人员变更，将日志状态改为 STAFF_CHANGE*/
        update.setOperateTypeSupplier(() -> {
            Predicate<FruitDict.Systems> userPredicate = (status) -> update.getUserRelation().containsKey(status) && !update.getUserRelation().get(status).isEmpty();
            if (update.getUserRelation() != null && (userPredicate.test(FruitDict.Systems.ADD) || userPredicate.test(FruitDict.Systems.DELETE)))
                return FruitDict.LogsDict.STAFF_CHANGE;
            return FruitDict.LogsDict.UPDATE;
        });
        daoTask.modify(update);
        return newSuccess().setData(update.getUuid());
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
        return newSuccess().setData(vo.getUuidVo());
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
        return newSuccess().setData(vo.getUuidVo());
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
        return newSuccess().setData(vo.getUuidVo());
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
        return newSuccess().setData(vo.getUuidVo());
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
        return newSuccess().setData(vo.getUuidVo());
    }


    /**
     * @api {get} /v1/task/project/{uuid} 查询任务，携带所属列表
     * @apiVersion 0.1.0
     * @apiGroup task
     * @apiParam {String} uuid 项目uuid
     * @apiParam {String} listTitle 列表名称查询，支持前后模糊
     * @apiParam {String} title 任务名称查询，支持前后模糊
     */
    @RequestMapping(value = "/project/{uuid}", method = RequestMethod.GET)
    public RestResult findJoinProject(@PathVariable("uuid") String uuid,
                                      @JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        return newSuccess().setData(daoTask.findJoinProjects(uuid, vo));
    }

    /**
     * @api {get} /v1/task/{uuid} 任务详情
     * @apiVersion 0.1.0
     * @apiGroup task
     * @apiParam {String} uuid 任务uuid
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.GET)
    public RestResult findTaskInfo(@PathVariable("uuid") String uuid) {
        return newSuccess().setData(daoTask.findTaskInfo(uuid));
    }

    /**
     * @api {put} /v1/task/transfer/{uuid} 任务转交
     * @apiVersion 2.5.0
     * @apiGroup task
     * @apiParam {String} uuid 任务uuid
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
        return newSuccess().setData(uuid);
    }

    /************************************************************************************************
     *                                       个人中心专供                                            *
     ************************************************************************************************/

    /**
     * @api {get} /v1/task/current 当前用户-所有任务列表
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public RestResult myTask(@JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        return newSuccess().setData(daoTask.myTask(vo));
    }

    /**
     * @api {get} /v1/task/current_create 当前用户-创建的任务
     * @apiVersion 0.1.0
     * @apiGroup task
     */
    @RequestMapping(value = "/current_create", method = RequestMethod.GET)
    public RestResult userFinds(@JsonArgument(type = FruitTaskVo.class) FruitTaskVo vo) {
        return newSuccess().setData(daoTask.myCreateTask(vo));
    }

}
