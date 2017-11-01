package wowjoy.fruits.ms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.dao.user.AbstractDaoAccount;
import wowjoy.fruits.ms.dao.user.AbstractDaoUser;
import wowjoy.fruits.ms.dao.user.AccountDaoImpl;
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
    private AbstractDaoUser dataUser;
    @Resource(type = AccountDaoImpl.class)
    private AbstractDaoAccount daoAccount;

    @RequestMapping(method = RequestMethod.GET)
    public RestResult finds(@JsonArgument(type = FruitUserVo.class) FruitUserVo vo) {
        return RestResult.getInstance().setData(dataUser.finds(vo));
    }

    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public RestResult account(@JsonArgument(type = FruitUserVo.class) FruitUserVo vo) {
        daoAccount.inserts();
        return RestResult.getInstance();
    }

}
