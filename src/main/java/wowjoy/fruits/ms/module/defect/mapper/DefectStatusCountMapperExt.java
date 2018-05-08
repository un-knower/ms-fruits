package wowjoy.fruits.ms.module.defect.mapper;

import wowjoy.fruits.ms.module.defect.DefectStatusCount;

public interface DefectStatusCountMapperExt {
    public int insertOnDuplicatedUpdate(DefectStatusCount count);
}