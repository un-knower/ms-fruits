package wowjoy.fruits.ms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.dao.user.DataUserDaoImpl;
import wowjoy.fruits.ms.dao.user.UserArgument;
import wowjoy.fruits.ms.util.RestResult;

import java.util.Map;

/**
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping(method = RequestMethod.GET)
    public RestResult findByUser(@RequestParam Map data) {
        return RestResult.getInstance().setData(UserArgument.getInstance(data).setDataSource(DataUserDaoImpl.class).findByUser());
    }
}
