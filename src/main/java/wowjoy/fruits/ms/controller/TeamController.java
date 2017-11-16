package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.dao.team.AbstractDaoTeam;
import wowjoy.fruits.ms.module.task.FruitTaskVo;
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

    @RequestMapping(value = "/relation", method = RequestMethod.GET)
    public RestResult findRelation(@JsonArgument(type = FruitTeamVo.class) FruitTeamVo vo) {
        return RestResult.getInstance().setData(teamDao.findRelation(vo));
    }

    @RequestMapping(method = RequestMethod.GET)
    public RestResult finds(@JsonArgument(type = FruitTeamVo.class) FruitTeamVo vo) {
        return RestResult.getInstance().setData(teamDao.finds(vo));
    }

    @RequestMapping(method = RequestMethod.POST)
    public RestResult insert(@JsonArgument(type = FruitTeamVo.class) FruitTeamVo vo) {
        teamDao.insert(vo);
        return RestResult.getInstance().setData(vo.getUuid());
    }

    @RequestMapping(value = "{uuid}", method = RequestMethod.PUT)
    public RestResult update(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitTeamVo.class) FruitTeamVo vo) {
        vo.setUuidVo(uuid);
        teamDao.update(vo);
        return RestResult.getInstance().setData(uuid);
    }

    @RequestMapping(value = "{uuid}", method = RequestMethod.DELETE)
    public RestResult delete(@PathVariable("uuid") String uuid) {
        teamDao.delete(uuid);
        return RestResult.getInstance().setData(uuid);
    }

}
