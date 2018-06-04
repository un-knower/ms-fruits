package wowjoy.fruits.ms.module.versions.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.versions.FruitVersions;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public interface FruitVersionsMapperExt {
    long selectSonCount(@Param("versionId") String versionId);

    long selectJoinDefectCount(@Param("versionId") String versionId);

    ArrayList<String> selectJoinDefect(@Param("versionIds") ArrayList<String> versionIds);

    ArrayList<FruitVersions> selectByProjectId(@Param("projectId") String projectId, @Param("version") String version);

    ArrayList<FruitVersions> selectByProjectAndParentIds(@Param("projectId") String projectId, @Param("parentIds") LinkedList<String> parentIds, @Param("version") String version);
}
