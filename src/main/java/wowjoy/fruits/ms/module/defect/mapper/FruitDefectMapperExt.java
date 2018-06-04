package wowjoy.fruits.ms.module.defect.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.defect.DefectDuplicate;
import wowjoy.fruits.ms.module.defect.FruitDefect;

import java.util.ArrayList;

public interface FruitDefectMapperExt {

    DefectDuplicate selectDuplicate(@Param("uuid") String uuid);

    ArrayList<FruitDefect> selectByExampleExt(FruitDefect.Search search);
}