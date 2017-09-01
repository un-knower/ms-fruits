package wowjoy.fruits.ms.controller;

import org.springframework.web.bind.annotation.*;
import wowjoy.fruits.ms.dao.user.AbstractDaoUser;
import wowjoy.fruits.ms.dao.user.HRDataParset;
import wowjoy.fruits.ms.dao.user.UserDaoImpl;
import wowjoy.fruits.ms.util.RestResult;

import javax.annotation.Resource;
import java.util.Map;

/**
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource(type = UserDaoImpl.class)
    private AbstractDaoUser dataUserDao;

    @RequestMapping(value = "build", method = RequestMethod.POST)
    public RestResult build() {
        return RestResult.getInstance().setData(dataUserDao.build());
    }

    @RequestMapping(method = RequestMethod.GET)
    public RestResult findByUser(@RequestParam Map data) {
        return null;
    }

    @RequestMapping(value = "{userId}", method = RequestMethod.GET)
    public RestResult findByUserId(@PathVariable String userId) {
        return null;
    }
}
