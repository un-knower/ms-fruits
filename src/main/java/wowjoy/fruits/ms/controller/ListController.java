package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.aspectj.LogInfo;
import wowjoy.fruits.ms.dao.list.AbstractDaoList;
import wowjoy.fruits.ms.module.list.FruitList;
import wowjoy.fruits.ms.module.list.FruitListVo;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

/**
 * Created by wangziwen on 2017/10/17.
 */
@RestController
@RequestMapping("/v1/list")
public class ListController {


    @Qualifier("listDaoImpl")
    @Autowired
    private AbstractDaoList listDao;

    /**
     * @api {put} /v1/list/{uuid} 修改列表
     * @apiVersion 0.1.0
     * @apiGroup list
     */
    @LogInfo(uuid = "uuidVo", type = FruitDict.Parents.List, operateType = FruitDict.LogsDict.UPDATE)
    @RequestMapping(value = "{uuid}", method = RequestMethod.PUT)
    public RestResult update(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitListVo.class) FruitListVo vo) {
        vo.setUuidVo(uuid);
        listDao.update(vo);
        return RestResult.newSuccess().setData(vo.getUuidVo());
    }

    /**
     * @api {delete} /v1/list/{uuid} 删除列表
     * @apiVersion 0.1.0
     * @apiGroup list
     */
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.List, operateType = FruitDict.LogsDict.DELETE)
    @RequestMapping(value = "{uuid}", method = RequestMethod.DELETE)
    public RestResult delete(@PathVariable("uuid") String uuid) {
        FruitListVo vo = FruitList.getVo();
        vo.setUuidVo(uuid);
        listDao.delete(vo);
        return RestResult.newSuccess().setData(vo.getUuidVo());
    }

    /**
     * @api {post} /v1/list/project 项目-添加列表
     * @apiVersion 0.1.0
     * @apiGroup list
     * @apiExample {json} 项目添加示例
     *
    {
        "title":"测试列表添加日志记录功能",
        "description":"测试列表添加日志记录功能",
        "projectRelation":{"ADD":["e41e0c03ee704b31b56f2ec1076609b5"]}
    }
     */
    @LogInfo(uuid = "uuid", type = FruitDict.Parents.List, operateType = FruitDict.LogsDict.ADD)
    @RequestMapping(value = "project", method = RequestMethod.POST)
    public RestResult insertProject(@JsonArgument(type = FruitListVo.class) FruitListVo vo) {
        listDao.insertProject(vo);
        return RestResult.newSuccess().setData(vo);
    }
}
