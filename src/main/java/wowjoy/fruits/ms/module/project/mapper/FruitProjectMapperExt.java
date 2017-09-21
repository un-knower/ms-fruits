package wowjoy.fruits.ms.module.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.project.FruitProjectDao;
import wowjoy.fruits.ms.module.project.FruitProjectExample;

/**
 * Created by wangziwen on 2017/9/12.
 */
@Mapper
public interface FruitProjectMapperExt {
    List<FruitProjectDao> selectUserRelationByExample(FruitProjectExample example);
}
