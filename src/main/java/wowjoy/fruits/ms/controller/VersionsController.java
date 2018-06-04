package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.aspectj.LogInfo;
import wowjoy.fruits.ms.dao.versions.ServiceVersions;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.versions.FruitVersions;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
@RestController
@RequestMapping("/v1/versions")
public class VersionsController {

    private ServiceVersions serviceVersions;

    @Autowired
    public VersionsController(ServiceVersions serviceVersions) {
        this.serviceVersions = serviceVersions;
    }

    /**
     * @api {post} /v1/versions 添加版本
     * @apiVersion 3.0.0
     * @apiGroup versions
     */
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.VERSIONS, operateType = FruitDict.LogsDict.ADD)
    @RequestMapping(method = RequestMethod.POST)
    public RestResult addVersions(@JsonArgument(type = FruitVersions.Insert.class) FruitVersions.Insert insert) {
        this.serviceVersions.addVersion(insert);
        return RestResult.newSuccess().setData(insert.getUuid());
    }

    /**
     * @api {put} /v1/versions/{uuid} 修改版本
     * @apiVersion 3.0.0
     * @apiGroup versions
     */
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.VERSIONS, operateType = FruitDict.LogsDict.UPDATE)
    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public RestResult updateVersion(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitVersions.Update.class) FruitVersions.Update update) {
        update.setUuid(uuid);
        this.serviceVersions.updateVersion(update);
        return RestResult.newSuccess().setData(update.getUuid());
    }

    /**
     * @api {get} /v1/versions/project/{projectId} 查询版本列表 (分页)
     * @apiVersion 3.0.0
     * @apiGroup versions
     */
    @RequestMapping(value = "/project/{projectId}", method = RequestMethod.GET)
    public RestResult findPage(@PathVariable("projectId") String projectId, @JsonArgument(type = FruitVersions.Search.class) FruitVersions.Search search) {
        search.setProjectId(projectId);
        return RestResult.newSuccess().setData(this.serviceVersions.findPage(search));
    }

    /**
     * @api {get} /v1/versions/project/sons/{projectId} 子版本列表查询
     * @apiVersion 3.0.0
     * @apiGroup versions
     */
    @RequestMapping(value = "/project/sons/{projectId}", method = RequestMethod.GET)
    public RestResult findSons(@PathVariable("projectId") String projectId) {
        return RestResult.newSuccess().setData(this.serviceVersions.findSons(projectId));
    }

    @RequestMapping(value = "{uuid}", method = RequestMethod.GET)
    public RestResult find(@PathVariable("uuid") String uuid) {
        return RestResult.newSuccess().setData(this.serviceVersions.find(uuid));
    }

    @RequestMapping(value = "{uuid}", method = RequestMethod.DELETE)
    public RestResult remove(@PathVariable("uuid") String uuid) {
        this.serviceVersions.remove(uuid);
        return RestResult.newSuccess().setData(uuid);
    }

}
