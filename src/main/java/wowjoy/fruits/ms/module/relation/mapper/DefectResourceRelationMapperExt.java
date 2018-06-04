package wowjoy.fruits.ms.module.relation.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.relation.entity.DefectResourceRelation;
import wowjoy.fruits.ms.module.relation.entity.NotepadResourceRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.ArrayList;
import java.util.List;

public interface DefectResourceRelationMapperExt {

    ArrayList<DefectResourceRelation> selectByDefectId(@Param("type") FruitDict.Resource type, @Param("defectId") String defectId);
}