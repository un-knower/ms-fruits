package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.controller.vo.FruitProjectVo;
import wowjoy.fruits.ms.dao.project.AbstractDaoProject;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

/**
 * Created by wangziwen on 2017/9/6.
 */
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Qualifier("projectDaoImpl")
    @Autowired
    private AbstractDaoProject projectDaoImpl;

    @RequestMapping(method = RequestMethod.GET)
    public RestResult finds(@JsonArgument(type = FruitProjectVo.class) FruitProjectVo project) {
        return RestResult.getInstance().setData(projectDaoImpl.finds(project));
    }

    @RequestMapping(value = "{uuid}", method = RequestMethod.GET)
    public RestResult findByUUID(@PathVariable("uuid") String uuid) {
        return RestResult.getInstance().setData(projectDaoImpl.findByUUID(uuid));
    }

    @RequestMapping(method = RequestMethod.POST)
    public RestResult insert(@JsonArgument(type = FruitProjectVo.class) FruitProjectVo projectVo) {
        projectDaoImpl.insert(projectVo);
        return RestResult.getInstance().setData(projectVo.getUuid());
    }

    @RequestMapping(value = "{uuid}", method = RequestMethod.PUT)
    public RestResult update(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitProjectVo.class) FruitProjectVo projectVo) {
        projectVo.setUuid(uuid);
        projectDaoImpl.update(projectVo);
        return RestResult.getInstance().setData(projectVo.getUuid());
    }

    @RequestMapping(value = "underway/{uuid}", method = RequestMethod.PUT)
    public RestResult underway(@PathVariable("uuid") String uuid) {
        projectDaoImpl.updateStatus(uuid, FruitDict.ProjectDict.UNDERWAY);
        return RestResult.getInstance().setData(uuid);
    }

    @RequestMapping(value = "complete/{uuid}", method = RequestMethod.PUT)
    public RestResult complete(@PathVariable("uuid") String uuid) {
        projectDaoImpl.updateStatus(uuid, FruitDict.ProjectDict.COMPLETE);
        return RestResult.getInstance().setData(uuid);
    }
}
