package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.aspectj.LogInfo;
import wowjoy.fruits.ms.dao.project.AbstractDaoProject;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.project.FruitProjectVo;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

/**
 * 增加项目拼音搜索
 */
@RestController
@RequestMapping("/v1/project")
public class ProjectController {


    private final AbstractDaoProject projectDaoImpl;

    @Autowired
    public ProjectController(@Qualifier("projectDaoImpl") AbstractDaoProject projectDaoImpl) {
        this.projectDaoImpl = projectDaoImpl;
    }

    /**
     * @api {get} /v1/project/list/{uuid} 项目列表
     * @apiVersion 2.5.0
     * @apiGroup project
     * @apiParam {String} uuid 项目uuid
     */
    @RequestMapping(value = "/list/{uuid}", method = RequestMethod.GET)
    public RestResult findList(@PathVariable("uuid") String uuid) {
        return RestResult.newSuccess().setData(projectDaoImpl.findListByProjectId(uuid));
    }

    /**
     * @api {get} /v1/project/relation 项目查询
     * @apiVersion 0.1.0
     * @apiGroup project
     */
    @RequestMapping(value = "/relation", method = RequestMethod.GET)
    public RestResult findRelation(@JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        return RestResult.newSuccess().setData(projectDaoImpl.finds(vo));
    }

    /**
     * @api {get} /v1/project/user/{uuid} 查询项目所有用户成员
     * @apiVersion 2.5.0
     * @apiGroup project
     * @apiParam {String} uuid 项目uuid
     */
    @RequestMapping(value = "/user/{uuid}", method = RequestMethod.GET)
    public RestResult treeTeamUserList(@PathVariable("uuid") String uuid) {
        return RestResult.newSuccess().setData(projectDaoImpl.treeTeamUserList(uuid));
    }

    /**
     * @api {get} /v1/project/{uuid} 项目查询【详情】
     * @apiVersion 0.1.0
     * @apiGroup project
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.GET)
    public RestResult findByUUID(@PathVariable("uuid") String uuid) {
        FruitProjectVo projectVo = FruitProject.getVo();
        projectVo.setUuidVo(uuid);
        return RestResult.newSuccess().setData(projectDaoImpl.find(projectVo));
    }

    /**
     * @api {post} /v1/project 项目添加
     * @apiVersion 0.1.0
     * @apiGroup project
     */
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.PROJECT, operateType = FruitDict.LogsDict.ADD)
    @RequestMapping(method = RequestMethod.POST)
    public RestResult insert(@JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        projectDaoImpl.add(vo);
        return RestResult.newSuccess().setData(vo.getUuid());
    }

    /**
     * @api {put} /v1/project/{uuid} 项目修改
     * @apiVersion 0.1.0
     * @apiGroup project
     */
    @LogInfo(uuid = "uuidVo", type = FruitDict.Parents.PROJECT, operateType = FruitDict.LogsDict.UPDATE)
    @RequestMapping(value = "{uuid}", method = RequestMethod.PUT)
    public RestResult update(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitProject.Update.class) FruitProject.Update vo) {
        vo.setUuid(uuid);
        projectDaoImpl.modify(vo);
        return RestResult.newSuccess().setData(vo.getUuid());
    }

    /**
     * @api {put} /v1/project/complete/{uuid} 项目状态【完成】
     * @apiVersion 0.1.0
     * @apiGroup project
     */
    @LogInfo(uuid = "uuidVo", type = FruitDict.Parents.PROJECT, operateType = FruitDict.LogsDict.COMPLETE)
    @RequestMapping(value = "/complete/{uuid}", method = RequestMethod.PUT)
    public RestResult updateStatus(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        vo.setUuidVo(uuid);
        projectDaoImpl.complete(vo);
        return RestResult.newSuccess().setData(uuid);
    }

    /**
     * @api {delete} /v1/project/{uuid} 删除项目
     * @apiVersion 0.1.0
     * @apiGroup project
     */
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.PROJECT, operateType = FruitDict.LogsDict.DELETE)
    @RequestMapping(value = "{uuid}", method = RequestMethod.DELETE)
    public RestResult delete(@PathVariable("uuid") String uuid) {
        FruitProjectVo vo = FruitProject.getVo();
        vo.setUuidVo(uuid);
        projectDaoImpl.delete(vo);
        return RestResult.newSuccess().setData(uuid);
    }

    /*****************
     * 当前用户接口    *
     *****************/

    /**
     * @api {get} /v1/project/current 当前用户-项目查询
     * @apiVersion 2.5.0
     * @apiGroup project
     */
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public RestResult findCurrent(@JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        return RestResult.newSuccess().setData(projectDaoImpl.findsCurrentUser(vo));
    }

    /**
     * @api {get} /v1/project/create-task-come-from-projects 当前用户-创建的任务所在的项目列表
     * @apiVersion 2.5.0
     * @apiGroup project
     */
    @RequestMapping(value = "/create-task-come-from-projects", method = RequestMethod.GET)
    public RestResult createTaskComeFromProjects() {
        return RestResult.newSuccess().setData(projectDaoImpl.myCreateTaskFromProjects());
    }

    /**
     * @api {post} /v1/project/star/{projectId} 星标
     * @apiVersion 3.0.0
     * @apiGroup project
     * @apiParam {projectId} projectId 项目uuid
     */
    @RequestMapping(value = "/star/{projectId}", method = RequestMethod.POST)
    public RestResult star(@PathVariable("projectId") String projectId) {
        projectDaoImpl.star(projectId);
        return RestResult.newSuccess().setData(projectId);
    }

    /**
     * @api {delete} /v1/project/unStar/{projectId} 取消星标
     * @apiVersion 3.0.0
     * @apiGroup project
     * @apiParam {projectId} projectId 项目uuid
     */
    @RequestMapping(value = "/unStar/{projectId}", method = RequestMethod.DELETE)
    public RestResult unStar(@PathVariable("projectId") String projectId) {
        projectDaoImpl.unStar(projectId);
        return RestResult.newSuccess().setData(projectId);
    }
}
