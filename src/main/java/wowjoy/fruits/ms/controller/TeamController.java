package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.dao.team.AbstractDaoTeam;
import wowjoy.fruits.ms.module.team.FruitTeam;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

/**
 * Created by wangziwen on 2017/9/5.
 */
@RestController
@RequestMapping("/team")
public class TeamController {

    @Qualifier("teamDaoImpl")
    @Autowired
    private AbstractDaoTeam teamDao;

    @RequestMapping(method = RequestMethod.GET)
    public RestResult findByTeam(@JsonArgument(type = FruitTeam.class) FruitTeam team){
        return RestResult.getInstance().setData(teamDao.findByTeam(team));
    }

}