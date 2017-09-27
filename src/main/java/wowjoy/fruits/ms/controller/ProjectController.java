package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.dao.project.AbstractProject;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.project.FruitProjectVo;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

/**
 *
 */
@RestController
@RequestMapping("/v1/api/project")
public class ProjectController {


    @Qualifier("projectDaoImpl")
    @Autowired
    private AbstractProject projectDaoImpl;

    @RequestMapping(value = "/relation", method = RequestMethod.GET)
    public RestResult findRelation(@JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        return RestResult.getInstance().setData(projectDaoImpl.findRelation(vo));
    }

    @RequestMapping(method = RequestMethod.GET)
    public RestResult finds(@JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        return RestResult.getInstance().setData(projectDaoImpl.finds(vo));
    }

    @RequestMapping(value = "{uuid}", method = RequestMethod.GET)
    public RestResult findByUUID(@PathVariable("uuid") String uuid) {
        FruitProjectVo projectVo = FruitProject.getProjectVo();
        projectVo.setUuidVo(uuid);
        return RestResult.getInstance().setData(projectDaoImpl.findByUUID(projectVo));
    }

    @RequestMapping(method = RequestMethod.POST)
    public RestResult insert(@JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        projectDaoImpl.insert(vo);
        return RestResult.getInstance().setData(vo.getUuid());
    }

    @RequestMapping(value = "{uuid}", method = RequestMethod.PUT)
    public RestResult update(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        vo.setUuidVo(uuid);
        projectDaoImpl.update(vo);
        return RestResult.getInstance().setData(vo.getUuid());
    }

    @RequestMapping(value = "/status/{uuid}", method = RequestMethod.PUT)
    public RestResult updateStatus(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        vo.checkStatus();
        vo.setUuidVo(uuid);
        projectDaoImpl.updateStatus(vo);
        return RestResult.getInstance().setData(uuid);
    }
}
