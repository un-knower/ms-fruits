package wowjoy.fruits.ms.module.notepad.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.notepad.FruitNotepadDao;
import wowjoy.fruits.ms.module.notepad.FruitNotepadExample;

import java.util.List;

/**
 * Created by wangziwen on 2017/12/13.
 */
public interface FruitNotepadMapperExt {

    List<FruitNotepadDao> selectByTeamIds(@Param("example") FruitNotepadExample example, @Param("teamIds") String... teamIds);

}
