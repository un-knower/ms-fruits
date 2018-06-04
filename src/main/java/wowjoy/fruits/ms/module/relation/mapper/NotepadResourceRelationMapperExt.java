package wowjoy.fruits.ms.module.relation.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.resource.FruitResource;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.ArrayList;

public interface NotepadResourceRelationMapperExt {
    ArrayList<String> selectByNotepadId(@Param("type") FruitDict.Resource type, @Param("notepadId") String notepadId);
}