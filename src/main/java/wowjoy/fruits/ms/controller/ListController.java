package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.dao.list.AbstractDaoList;
import wowjoy.fruits.ms.module.list.FruitList;
import wowjoy.fruits.ms.module.list.FruitListVo;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

/**
 * Created by wangziwen on 2017/10/17.
 */
@RestController
@RequestMapping("/v1/api/list")
public class ListController {


    @Qualifier("listDaoImpl")
    @Autowired
    private AbstractDaoList listDao;

    /**
     * 查询任务列表
     *
     * @param vo
     * @return
     */
    @RequestMapping(value = "/task", method = RequestMethod.GET)
    public RestResult findTasks(@JsonArgument(type = FruitListVo.class) FruitListVo vo) {
        return RestResult.getInstance().setData(listDao.findTask(vo));
    }

    /**
     * 添加任务列表
     *
     * @param vo
     * @return
     */
    @RequestMapping(value = "/task", method = RequestMethod.POST)
    public RestResult insertTask(@JsonArgument(type = FruitListVo.class) FruitListVo vo) {
        listDao.insertTask(vo);
        return RestResult.getInstance().setData(vo.getUuid());
    }

    /**
     * 修改列表
     *
     * @param uuid
     * @param vo
     * @return
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.PUT)
    public RestResult update(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitListVo.class) FruitListVo vo) {
        vo.setUuidVo(uuid);
        listDao.update(vo);
        return RestResult.getInstance().setData(vo.getUuidVo());
    }

    /**
     * 删除列表-物理删除
     *
     * @param uuid
     * @return
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.DELETE)
    public RestResult delete(@PathVariable("uuid") String uuid) {
        FruitListVo vo = FruitList.getVo();
        vo.setUuidVo(uuid);
        listDao.delete(vo);
        return RestResult.getInstance().setData(vo.getUuidVo());
    }
}
