package wowjoy.fruits.ms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import wowjoy.fruits.ms.dao.user.AbstractDaoAccount;
import wowjoy.fruits.ms.dao.user.AbstractDaoUser;
import wowjoy.fruits.ms.dao.user.AccountDaoImpl;
import wowjoy.fruits.ms.dao.user.UserDaoImpl;
import wowjoy.fruits.ms.module.user.FruitAccountVo;
import wowjoy.fruits.ms.module.user.FruitUserVo;
import wowjoy.fruits.ms.util.ApplicationContextUtils;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

import javax.annotation.Resource;

/**
 */
@RestController
@RequestMapping("/v1/api/user")
public class UserController {
    @Resource(type = AccountDaoImpl.class)
    private AbstractDaoAccount daoAccount;

    @RequestMapping(method = RequestMethod.GET)
    public RestResult finds() {
        return RestResult.getInstance().setData(ApplicationContextUtils.getCurrentUser());
    }

    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public RestResult account(@JsonArgument(type = FruitAccountVo.class) FruitAccountVo vo) {
        return RestResult.getInstance().setData(daoAccount.findByAccount(vo));
    }
}
