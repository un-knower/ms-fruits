package wowjoy.fruits.ms.module.resource.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.defect.FruitDefectResource;

import java.util.ArrayList;

public interface FruitResourceMapperExt {
    /**
     * 当前接口只针对缺陷模块开发，其他功能点使用时请仔细阅读sql语句是否符合需求
     * 根据defectIds筛选所有符合条件的资源
     * 若resourceIds不为空，则进步一根据限定资源id查询，资源id查询只针对描述资源
     *
     * @param defectIds
     * @return
     */
    ArrayList<FruitDefectResource> selectByDefectId(@Param("defectIds") ArrayList<String> defectIds);
}