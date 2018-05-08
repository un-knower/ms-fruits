package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.aspectj.LogInfo;
import wowjoy.fruits.ms.dao.defect.ServiceDefect;
import wowjoy.fruits.ms.module.comment.FruitComment;
import wowjoy.fruits.ms.module.defect.FruitDefect;
import wowjoy.fruits.ms.module.defect.FruitDefect.ChangeInfo;
import wowjoy.fruits.ms.module.defect.FruitDefect.Search;
import wowjoy.fruits.ms.module.defect.FruitDefect.Update;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
@RestController
@RequestMapping("/v1/defect")
public class DefectController {

    private final ServiceDefect serviceDefect;

    @Autowired
    public DefectController(ServiceDefect serviceDefect) {
        this.serviceDefect = serviceDefect;
    }

    /*添加接口*/
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.DEFECT, operateType = FruitDict.LogsDict.ADD)
    @RequestMapping(method = RequestMethod.POST)
    public RestResult insert(@JsonArgument(type = FruitDefect.Insert.class) FruitDefect.Insert insert) {
        serviceDefect.beforeInsert(insert);
        return RestResult.newSuccess().setData(insert.getUuid());
    }

    /*修改接口*/
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.DEFECT, operateType = FruitDict.LogsDict.UPDATE)
    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public RestResult update(@PathVariable("uuid") String uuid, @JsonArgument(type = Update.class) Update update) {
        update.setUuid(uuid);
        serviceDefect.beforeUpdate(update);
        return RestResult.newSuccess().setData(uuid);
    }

    /*列表接口*/
    @RequestMapping(value = "project/{projectId}", method = RequestMethod.GET)
    public RestResult finds(@PathVariable("projectId") String projectId, @JsonArgument(type = Search.class) Search search) {
        search.setProjectId(projectId);
        return RestResult.newSuccess().setData(serviceDefect.finds(search).toPageInfo());
    }

    /*当前用户创建的缺陷*/
    @RequestMapping(value = "current/create", method = RequestMethod.GET)
    public RestResult currentCreate(@JsonArgument(type = Search.class) Search search) {
        search.setUserId(ApplicationContextUtils.getCurrentUser().getUserId());
        return RestResult.newSuccess().setData(serviceDefect.finds(search).toPageInfo());
    }

    /*当前用户处理的缺陷*/
    @RequestMapping(value = "current/handler", method = RequestMethod.GET)
    public RestResult currentHandler(@JsonArgument(type = Search.class) Search search) {
        search.setHandlerUserId(ApplicationContextUtils.getCurrentUser().getUserId());
        return RestResult.newSuccess().setData(serviceDefect.finds(search).toPageInfo());
    }

    /*详情接口*/
    @RequestMapping(value = "{uuid}", method = RequestMethod.GET)
    public RestResult find(@PathVariable("uuid") String uuid) {
        return RestResult.newSuccess().setData(serviceDefect.find(uuid));
    }

    /*已解决*/
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.DEFECT, operateType = FruitDict.LogsDict.TO_SOLVED)
    @RequestMapping(value = "/solved/{uuid}", method = RequestMethod.PUT)
    public RestResult toSolved(@PathVariable("uuid") String uuid, @JsonArgument(type = ChangeInfo.class) ChangeInfo change) {
        change.setUuid(uuid);
        serviceDefect.toSolved(change);
        return RestResult.newSuccess().setData(uuid);
    }

    /*已关闭*/
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.DEFECT, operateType = FruitDict.LogsDict.TO_CLOSED)
    @RequestMapping(value = "/closed/{uuid}", method = RequestMethod.PUT)
    public RestResult toClosed(@PathVariable("uuid") String uuid, @JsonArgument(type = ChangeInfo.class) ChangeInfo change) {
        change.setUuid(uuid);
        serviceDefect.toClosed(change);
        return RestResult.newSuccess().setData(uuid);
    }

    /*不予处理*/
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.DEFECT, operateType = FruitDict.LogsDict.TO_DISREGARD)
    @RequestMapping(value = "/disregard/{uuid}", method = RequestMethod.PUT)
    public RestResult toDisregard(@PathVariable("uuid") String uuid, @JsonArgument(type = ChangeInfo.class) ChangeInfo change) {
        change.setUuid(uuid);
        serviceDefect.toDisregard(change);
        return RestResult.newSuccess().setData(uuid);
    }

    /*延期处理*/
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.DEFECT, operateType = FruitDict.LogsDict.TO_DELAY)
    @RequestMapping(value = "/delay/{uuid}", method = RequestMethod.PUT)
    public RestResult toDelay(@PathVariable("uuid") String uuid, @JsonArgument(type = ChangeInfo.class) ChangeInfo change) {
        change.setUuid(uuid);
        serviceDefect.toDelay(change);
        return RestResult.newSuccess().setData(uuid);
    }

    /*重新打开*/
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.DEFECT, operateType = FruitDict.LogsDict.TO_REOPEN)
    @RequestMapping(value = "/reopen/{uuid}", method = RequestMethod.PUT)
    public RestResult toReOpen(@PathVariable("uuid") String uuid, @JsonArgument(type = ChangeInfo.class) ChangeInfo change) {
        change.setUuid(uuid);
        serviceDefect.toReOpen(change);
        return RestResult.newSuccess().setData(uuid);
    }

    /*缺陷评论*/
    @RequestMapping(value = "comment/{uuid}", method = RequestMethod.POST)
    public RestResult comment(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitComment.Insert.class) FruitComment.Insert insert) {
        serviceDefect.insertComment(insert, uuid);
        return RestResult.newSuccess().setData(insert.getUuid());
    }

}
