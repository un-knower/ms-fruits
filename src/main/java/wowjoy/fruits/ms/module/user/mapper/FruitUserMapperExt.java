package wowjoy.fruits.ms.module.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.user.FruitUser;

@Mapper
public interface FruitUserMapperExt {
    void inserts(@Param("inserts") FruitUser[] users);

}