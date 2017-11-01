package wowjoy.fruits.ms.module.user.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.user.FruitAccount;
import wowjoy.fruits.ms.module.user.FruitAccountDao;
import wowjoy.fruits.ms.module.user.example.FruitAccountExample;

import java.util.List;

/**
 * Created by wangziwen on 2017/10/26.
 */
public interface FruitAccountMapperExt {
    void inserts(@Param("accounts") List<FruitAccountDao> accounts);

    List<FruitAccountDao> relationUser(FruitAccountExample example);
}
