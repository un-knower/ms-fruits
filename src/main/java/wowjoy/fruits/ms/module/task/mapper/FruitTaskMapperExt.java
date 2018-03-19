package wowjoy.fruits.ms.module.task.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.list.FruitListExample;
import wowjoy.fruits.ms.module.task.FruitTaskDao;
import wowjoy.fruits.ms.module.task.FruitTaskExample;

import java.util.List;

/**
 * Created by wangziwen on 2017/8/31.
 */
@Mapper
public interface FruitTaskMapperExt {
    List<FruitTaskDao> selectByListExampleAndProjectId(@Param("example") FruitTaskExample example, @Param("listExample") FruitListExample listExample, @Param("projectId") String projectId);

    List<FruitTaskDao> selectJoinUserByTaskIds(@Param("ids") List<String> example);

    List<FruitTaskDao> selectPlanByTask(@Param("example") FruitTaskExample example);

    List<FruitTaskDao> selectProjectByTask(@Param("example") FruitTaskExample example);

    List<FruitTaskDao> selectPlanJoinProjectByTask(@Param("taskIds") List<String> taskIds);

    List<FruitTaskDao> selectListByTask(@Param("example") FruitTaskExample example);

    List<FruitTaskDao> selectByExampleAndUserIdAndProjectId(@Param("example") FruitTaskExample example, @Param("projectId") String projectId, @Param("userIds") List<String> userIds);

    /************************************************************************************************
     *                                       个人中心专供                                            *
     ************************************************************************************************/
    List<FruitTaskDao> myTaskByExample(@Param("example") FruitTaskExample example, @Param("userIds") List<String> userIds, @Param("projectId") String projectId);

    List<FruitTaskDao> myCreateTask(@Param("example") FruitTaskExample example, @Param("userId") String userId, @Param("projectId") String projectId);

}
