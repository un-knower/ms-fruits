package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.dao.team.AbstractDaoTeam;
import wowjoy.fruits.ms.module.team.FruitTeam;
import wowjoy.fruits.ms.module.team.FruitTeamVo;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

/**
 * Created by wangziwen on 2017/9/5.
 */
@RestController
@RequestMapping("/v1/team")
public class TeamController {

    @Qualifier("teamDaoImpl")
    @Autowired
    private AbstractDaoTeam teamDao;

    /**
     * @api {get} /v1/team/relation 团队信息查询
     * @apiVersion 0.1.0
     * @apiGroup team
     */
    @RequestMapping(value = "/relation", method = RequestMethod.GET)
    public RestResult findRelation(@JsonArgument(type = FruitTeamVo.class) FruitTeamVo vo) {
        return RestResult.getInstance().setData(teamDao.findRelation(vo));
    }

    /**
     * @api {get} /v1/team/current 团队信息查询【当前用户】
     * @apiVersion 0.1.0
     * @apiGroup team
     */
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public RestResult findCurrent(@JsonArgument(type = FruitTeamVo.class) FruitTeamVo vo) {
        return RestResult.getInstance().setData(teamDao.findCurrent(vo));
    }

    /**
     * @api {get} /v1/team/{uuid} 团队详情查询
     * @apiVersion 0.1.0
     * @apiGroup team
     */
    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    public RestResult findRelation(@PathVariable("uuid") String uuid) {
        FruitTeamVo vo = FruitTeam.getFruitTeamVo();
        vo.setUuidVo(uuid);
        return RestResult.getInstance().setData(teamDao.find(vo));
    }

    /**
     * @api {post} /v1/team 团队添加
     * @apiVersion 0.1.0
     * @apiGroup team
     */
    @RequestMapping(method = RequestMethod.POST)
    public RestResult insert(@JsonArgument(type = FruitTeamVo.class) FruitTeamVo vo) {
        teamDao.insert(vo);
        return RestResult.getInstance().setData(vo.getUuid());
    }

    /**
     * @api {put} /v1/team/{uuid} 团队修改
     * @apiVersion 0.1.0
     * @apiGroup team
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.PUT)
    public RestResult update(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitTeamVo.class) FruitTeamVo vo) {
        vo.setUuidVo(uuid);
        teamDao.update(vo);
        return RestResult.getInstance().setData(uuid);
    }

    /**
     * @api {delete} /v1/team/{uuid} 团队删除
     * @apiVersion 0.1.0
     * @apiGroup team
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.DELETE)
    public RestResult delete(@PathVariable("uuid") String uuid) {
        teamDao.delete(uuid);
        return RestResult.getInstance().setData(uuid);
    }

}
