package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.dao.user.AbstractDaoAccount;
import wowjoy.fruits.ms.dao.user.AbstractDaoUser;
import wowjoy.fruits.ms.dao.user.AccountDaoImpl;
import wowjoy.fruits.ms.module.user.FruitAccountVo;
import wowjoy.fruits.ms.module.user.FruitUserVo;
import wowjoy.fruits.ms.util.ApplicationContextUtils;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

import javax.annotation.Resource;

/**
 */
@RestController
@RequestMapping("/v1/user")
public class UserController {
    @Resource(type = AccountDaoImpl.class)
    private AbstractDaoAccount daoAccount;
    @Autowired
    private AbstractDaoUser userDao;


    @RequestMapping(method = RequestMethod.POST)
    public RestResult finds() {
        return RestResult.newSuccess().setData(ApplicationContextUtils.getCurrentUser());
    }

    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public RestResult account(@JsonArgument(type = FruitAccountVo.class) FruitAccountVo vo) {
        return RestResult.newSuccess().setData(daoAccount.findByAccount(vo));
    }

    @RequestMapping(method = RequestMethod.GET)
    public RestResult finds(@JsonArgument(type = FruitUserVo.class) FruitUserVo vo) {
        return RestResult.newSuccess().setData(userDao.finds(vo));
    }

}
