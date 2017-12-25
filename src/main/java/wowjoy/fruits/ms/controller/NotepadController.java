package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.aspectj.LogInfo;
import wowjoy.fruits.ms.dao.notepad.AbstractDaoNotepad;
import wowjoy.fruits.ms.module.notepad.FruitNotepadVo;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

/**
 * Created by wangziwen on 2017/12/8.
 */
@RestController
@RequestMapping("/v1/notepad")
public class NotepadController {

    @Autowired
    private AbstractDaoNotepad daoNotepad;

    /**
     * @api {post} /v1/notepad 添加日报
     * @apiVersion 0.1.0
     * @apiGroup notepad
     * @apiExample {json} 添加日报
     * {
     * "content":"今天我很生气，我要吃鸡",
     * "estimatedSubmitDate":"2017-10-20"
     * }
     */
    @LogInfo(uuid = "vo.uuid", type = FruitDict.Parents.NOTEPAD, operateType = FruitDict.LogsDict.ADD)
    @RequestMapping(method = RequestMethod.POST)
    public RestResult insert(@JsonArgument(type = FruitNotepadVo.class) FruitNotepadVo vo) {
        daoNotepad.insert(vo);
        return RestResult.getInstance().setData(vo.getUuid());
    }

    /**
     * @api {put} /v1/notepad/{uuid} 修改日报
     * @apiVersion 0.1.0
     * @apiGroup notepad
     * @apiExample {json} 添加日报
     * {
     * "content":"今天我很生气，我要吃鸡"
     * }
     */
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.NOTEPAD, operateType = FruitDict.LogsDict.UPDATE)
    @RequestMapping(value = "{uuid}", method = RequestMethod.PUT)
    public RestResult update(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitNotepadVo.class) FruitNotepadVo vo) {
        vo.setUuidVo(uuid);
        daoNotepad.update(vo);
        return RestResult.getInstance().setData(vo.getUuidVo());
    }

    /**
     * @api {delete} /v1/notepad/{uuid} 删除日报
     * @apiVersion 0.1.0
     * @apiGroup notepad
     * @apiParam {String} uuid 日报uuid
     */
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.NOTEPAD, operateType = FruitDict.LogsDict.DELETE)
    @RequestMapping(value = "{uuid}", method = RequestMethod.DELETE)
    public RestResult delete(@PathVariable("uuid") String uuid) {
        daoNotepad.delete(uuid);
        return RestResult.getInstance().setData(uuid);
    }

    /**
     * @api {get} /v1/notepad/user 查看日报（当前登录用户）
     * @apiVersion 0.1.0
     * @apiGroup notepad
     * @apiParam {Date} startDate 开始日期 (范围查询，配合结束日期使用)
     * @apiParam {Date} endDate 结束日期
     * @apiParam {String} state 状态 PUNCTUAL_SUBMIT("准时提交"),PAY_SUBMIT("补交"),NOT_SUBMIT("未提交")
     */

    @RequestMapping(value = "user", method = RequestMethod.GET)
    public RestResult findCurrentUser(@JsonArgument(type = FruitNotepadVo.class) FruitNotepadVo vo) {
        return RestResult.getInstance().setData(daoNotepad.findByCurrentUser(vo));
    }

    /**
     * @api {get} /v1/notepad/team/{teamId} 查看日报（团队视角）
     * @apiVersion 0.1.0
     * @apiGroup notepad
     * @apiParam {String} teamId 团队id
     * @apiParam {Date} startDate 开始日期 (范围查询，配合结束日期使用)
     * @apiParam {Date} endDate 结束日期
     * @apiParam {String} state 状态
     */
    @RequestMapping(value = "team/{teamId}", method = RequestMethod.GET)
    public RestResult find(@PathVariable("teamId") String teamId, @JsonArgument(type = FruitNotepadVo.class) FruitNotepadVo vo) {
        return RestResult.getInstance().setData(daoNotepad.findTeam(vo, teamId));
    }
}
