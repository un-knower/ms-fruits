package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.dao.project.AbstractDaoProject;
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
    private AbstractDaoProject projectDaoImpl;

    /**
     * 查询项目 and 关联信息
     * 2017年10月10日09:23:53-汪梓文：测试通过
     *
     * @param vo
     * @return
     */
    @RequestMapping(value = "/relation", method = RequestMethod.GET)
    public RestResult findRelation(@JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        return RestResult.getInstance().setData(projectDaoImpl.findRelation(vo));
    }

    /**
     * 查询项目信息
     * 2017年10月10日09:25:02-汪梓文：测试通过
     *
     * @param vo
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public RestResult finds(@JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        return RestResult.getInstance().setData(projectDaoImpl.finds(vo));
    }

    /**
     * 查询项目详情
     * 2017年10月10日09:25:44-汪梓文：缺少leader信息
     * 2017年10月10日09:57:55-汪梓文：⤴️错误已修改
     *
     * @param uuid
     * @return
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.GET)
    public RestResult findByUUID(@PathVariable("uuid") String uuid) {
        FruitProjectVo projectVo = FruitProject.getProjectVo();
        projectVo.setUuidVo(uuid);
        return RestResult.getInstance().setData(projectDaoImpl.findByUUID(projectVo));
    }

    /**
     * 添加项目
     * 2017年10月10日09:29:30-汪梓文：
     * 测试通过。
     * 待改进：遇到一个问题当传入的参数不包含在枚举中时，会出现"null"key的问题，包括大小写。
     *
     * @param vo
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public RestResult insert(@JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        projectDaoImpl.insert(vo);
        return RestResult.getInstance().setData(vo.getUuid());
    }

    /**
     * 修改项目、关联信息
     * 2017年10月10日09:46:50-汪梓文：
     * 测试通过。
     * 待改进：需要防止关联重复的信息。
     *
     * @param uuid
     * @param vo
     * @return
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.PUT)
    public RestResult update(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        vo.setUuidVo(uuid);
        projectDaoImpl.update(vo);
        return RestResult.getInstance().setData(vo.getUuid());
    }

    /**
     * 修改项目状态至已完成
     * 2017年10月10日09:50:29-汪梓文：测试通过
     *
     * @param uuid
     * @param vo
     * @return
     */
    @RequestMapping(value = "/complete/{uuid}", method = RequestMethod.PUT)
    public RestResult updateStatus(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitProjectVo.class) FruitProjectVo vo) {
        vo.setUuidVo(uuid);
        projectDaoImpl.complete(vo);
        return RestResult.getInstance().setData(uuid);
    }

    /**
     * 删除项目、关联信息
     * 2017年10月10日09:52:43-汪梓文：测试通过
     *
     * @param uuid
     * @return
     */
    @RequestMapping(value = "{uuid}", method = RequestMethod.DELETE)
    public RestResult delete(@PathVariable("uuid") String uuid) {
        FruitProjectVo vo = FruitProject.getProjectVo();
        vo.setUuidVo(uuid);
        projectDaoImpl.delete(vo);
        return RestResult.getInstance().setData(uuid);
    }
}
