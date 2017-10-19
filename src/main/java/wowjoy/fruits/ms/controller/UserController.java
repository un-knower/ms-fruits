package wowjoy.fruits.ms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.dao.user.AbstractDaoUser;
import wowjoy.fruits.ms.dao.user.UserDaoImpl;
import wowjoy.fruits.ms.module.user.FruitUserVo;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

import javax.annotation.Resource;

/**
 */
@RestController
@RequestMapping("/v1/api/user")
public class UserController {
    @Resource(type = UserDaoImpl.class)
    private AbstractDaoUser dataUserDao;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public RestResult find(@JsonArgument(type = FruitUserVo.class) FruitUserVo vo) {
        return RestResult.getInstance().setData(dataUserDao.login(vo));
    }

    @RequestMapping(method = RequestMethod.GET)
    public RestResult finds(@JsonArgument(type = FruitUserVo.class) FruitUserVo vo) {
        return RestResult.getInstance().setData(dataUserDao.finds(vo));
    }

}
