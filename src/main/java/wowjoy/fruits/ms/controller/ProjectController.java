package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.aspectj.LogInfo;
import wowjoy.fruits.ms.dao.project.AbstractDaoProject;
import wowjoy.fruits.ms.module.notepad.FruitNotepad;
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


    @Qualifier("projectDaoImpl")
    @Autowired
    private AbstractDaoProject projectDaoImpl;

    /**
     * @api {get} /v1/project/relation 项目查询【列表】
     * @apiVersion 0.1.0
     * @apiGroup project
     */
    @RequestMapping(value = "/relation", method = RequestMethod.GET)
    public RestResult findRelation(@JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        return RestResult.getInstance().setData(projectDaoImpl.finds(vo, true));
    }

    /**
     * @api {get} /v1/project/current 项目查询【列表，当前用户关联项目】
     * @apiVersion 0.1.0
     * @apiGroup project
     */
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public RestResult findCurrent(@JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        return RestResult.getInstance().setData(projectDaoImpl.findsCurrentUser(vo));
    }

    /**
     * @api {get} /v1/project/user/{uuid} 根据项目id，查询用户信息
     * @apiVersion 0.1.0
     * @apiGroup project
     */
    @RequestMapping(value = "/user/{uuid}", method = RequestMethod.GET)
    public RestResult findRelation(@PathVariable("uuid") String uuid) {
        return RestResult.getInstance().setData(projectDaoImpl.findUserByProjectId(uuid));
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
        return RestResult.getInstance().setData(projectDaoImpl.findByUUID(projectVo, true));
    }

    /**
     * @api {post} /v1/project 项目添加
     * @apiVersion 0.1.0
     * @apiGroup project
     */
    @LogInfo(uuid = "vo.uuid", type = FruitDict.Parents.PROJECT, operateType = FruitDict.LogsDict.ADD)
    @RequestMapping(method = RequestMethod.POST)
    public RestResult insert(@JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        projectDaoImpl.add(vo);
        return RestResult.getInstance().setData(vo.getUuid());
    }

    /**
     * @api {put} /v1/project/{uuid} 项目修改
     * @apiVersion 0.1.0
     * @apiGroup project
     */
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.PROJECT, operateType = FruitDict.LogsDict.UPDATE)
    @RequestMapping(value = "{uuid}", method = RequestMethod.PUT)
    public RestResult update(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        vo.setUuidVo(uuid);
        projectDaoImpl.modify(vo);
        return RestResult.getInstance().setData(vo.getUuid());
    }

    /**
     * @api {put} /v1/project/complete/{uuid} 项目状态【完成】
     * @apiVersion 0.1.0
     * @apiGroup project
     */
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.PROJECT, operateType = FruitDict.LogsDict.COMPLETE)
    @RequestMapping(value = "/complete/{uuid}", method = RequestMethod.PUT)
    public RestResult updateStatus(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        vo.setUuidVo(uuid);
        projectDaoImpl.complete(vo);
        return RestResult.getInstance().setData(uuid);
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
        return RestResult.getInstance().setData(uuid);
    }
}
