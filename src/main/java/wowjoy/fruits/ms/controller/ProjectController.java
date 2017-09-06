package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.dao.project.AbstractDaoProject;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.util.JsonArgument;

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
    public void findByProject(@JsonArgument(type = FruitProject.class)FruitProject project){
    }
}
