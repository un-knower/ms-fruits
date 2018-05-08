package wowjoy.fruits.ms.module.comment.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.comment.DefectComment;

import java.util.ArrayList;

public interface FruitCommentMapperExt {
    ArrayList<DefectComment> selectByDefectId(@Param("defectId") String defectId);
}