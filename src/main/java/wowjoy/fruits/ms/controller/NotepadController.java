package wowjoy.fruits.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wowjoy.fruits.ms.dao.notepad.AbstractDaoNotepad;
import wowjoy.fruits.ms.module.notepad.FruitNotepadVo;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

/**
 * Created by wangziwen on 2017/12/8.
 */
@RestController
@RequestMapping("/v1/notepad")
public class NotepadController {

    @Autowired
    private AbstractDaoNotepad daoNotepad;

    @RequestMapping(method = RequestMethod.POST)
    public RestResult insert(@JsonArgument(type = FruitNotepadVo.class) FruitNotepadVo vo) {
        daoNotepad.insert(vo);
        return RestResult.getInstance().setData(vo.getUuid());
    }

    @RequestMapping(value = "{uuid}", method = RequestMethod.PUT)
    public RestResult update(@PathVariable("uuid") String uuid, @JsonArgument(type = FruitNotepadVo.class) FruitNotepadVo vo) {
        vo.setUuidVo(uuid);
        daoNotepad.update(vo);
        return RestResult.getInstance().setData(vo.getUuidVo());
    }

    @RequestMapping(value = "{uuid}", method = RequestMethod.DELETE)
    public RestResult delete(@PathVariable("uuid") String uuid) {
        daoNotepad.delete(uuid);
        return RestResult.getInstance().setData(uuid);
    }

    @RequestMapping(method = RequestMethod.GET)
    public RestResult find(@JsonArgument(type = FruitNotepadVo.class) FruitNotepadVo vo) {
        return RestResult.getInstance().setData(daoNotepad.findByCurrentUser(vo));
    }
}
