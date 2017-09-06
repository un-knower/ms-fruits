package wowjoy.fruits.ms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.dao.user.AbstractDaoUser;
import wowjoy.fruits.ms.dao.user.UserDaoImpl;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserEmpty;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

import javax.annotation.Resource;

/**
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource(type = UserDaoImpl.class)
    private AbstractDaoUser dataUserDao;

    @RequestMapping(method = RequestMethod.POST)
    public RestResult findByUserId(@JsonArgument(type = FruitUser.class) FruitUser user) {
        final FruitUser data = dataUserDao.findByUser(user);
        if (!data.isNotEmpty()){
            final RestResult restResult = RestResult.getInstance();
            restResult.setSuccess(false);
            restResult.setCode(4000);
            restResult.setMsg(123);
            return restResult;
        }
        return RestResult.getInstance().setData(data);
    }
}
