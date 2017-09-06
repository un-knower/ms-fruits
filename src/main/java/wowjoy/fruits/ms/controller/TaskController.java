package wowjoy.fruits.ms.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.dao.task.AbstractDaoTask;
import wowjoy.fruits.ms.dao.task.TaskDaoImpl;
import wowjoy.fruits.ms.module.task.FruitTask;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

import javax.annotation.Resource;

/**
 * Created by wangziwen on 2017/8/30.
 */
@RestController
@RequestMapping("/task")
public class TaskController {
    @Resource(type = TaskDaoImpl.class)
    private AbstractDaoTask abstractDaoTask;

    @RequestMapping(method = RequestMethod.POST)
    public RestResult insert(@JsonArgument(type = FruitTask.class) FruitTask data) {
        abstractDaoTask.setTask(data).setUserId("sss").insert();
        return RestResult.getInstance().setData(data.getUuid());
    }

    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public RestResult findByUser(@PathVariable String userId, @JsonArgument(type = FruitTask.class) FruitTask datas) {
        return RestResult.getInstance().setData(abstractDaoTask.findByUser());
    }

}
