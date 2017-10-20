package wowjoy.fruits.ms.module.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.user.FruitUserExample;

import java.util.List;

@Mapper
public interface FruitUserMapperExt {
    void inserts(@Param("inserts") FruitUser[] users);

    List<FruitUserDao> selectByPlan(@Param("example") FruitUserExample example, @Param("planId") String planId);
}