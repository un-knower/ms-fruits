package wowjoy.fruits.ms.module.versions.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.versions.FruitVersions;
import wowjoy.fruits.ms.module.versions.FruitVersionsExample;

import java.util.ArrayList;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public interface FruitVersionsMapperExt {
    ArrayList<FruitVersions> selectByExampleOrUserExample(@Param("versionExample") FruitVersionsExample versionsExample, @Param("userExample") FruitUserExample userExample);
}
