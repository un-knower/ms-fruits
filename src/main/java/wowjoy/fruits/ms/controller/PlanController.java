package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.dao.plan.AbstractDaoPlan;
import wowjoy.fruits.ms.dao.plan.DataPlanDaoImpl;
import wowjoy.fruits.ms.module.plan.FruitPlan;
import wowjoy.fruits.ms.module.team.FruitTeam;
import wowjoy.fruits.ms.util.JsonArgument;

/**
 * Created by wangziwen on 2017/9/11.
 */
@RestController
@RequestMapping("/plan")
@Transactional
public class PlanController {


    @Qualifier("dataPlanDaoImpl")
    @Autowired
    private AbstractDaoPlan dataPlanDao;

    /**
     * 详情
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.GET)
    public void findByUUID(@PathVariable("uuid") String uuid) {

    }

    /**
     * 列表
     * 分页
     */
    @RequestMapping(method = RequestMethod.GET)
    public void findByUUID(@JsonArgument FruitPlan fruitPlan) {
//        dataPlanDao.finds(fruitPlan);
    }

    /**
     * 添加
     */
    @RequestMapping(method = RequestMethod.POST)
    public void insert(@JsonArgument FruitPlan fruitPlan) {

    }

    /**
     * 修改
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.PUT)
    public void findByUUID(@PathVariable("uuid") String uuid, @JsonArgument FruitPlan fruitPlan) {

    }


}
