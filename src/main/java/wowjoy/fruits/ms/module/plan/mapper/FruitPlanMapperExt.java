package wowjoy.fruits.ms.module.plan.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.plan.FruitPlan;
import wowjoy.fruits.ms.module.plan.FruitPlanUser;
import wowjoy.fruits.ms.module.plan.example.FruitPlanExample;
import wowjoy.fruits.ms.module.task.FruitTaskPlan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangziwen on 2017/9/13.
 */
public interface FruitPlanMapperExt {

    ArrayList<FruitPlan> selectByProjectId(@Param("example") FruitPlanExample example, @Param("projectId") String projectId);

    List<FruitPlanUser> selectUserByPlanIds(@Param("planIds") List<String> planIds, @Param("userId") String userId);

    List<FruitTaskPlan> selectPlanByTask(@Param("taskIn") ArrayList<String> taskIn);

    List<FruitPlanUser> selectUserByPlanExampleAndUserIdOrProjectId(@Param("example") FruitPlanExample example, @Param("projectId") String projectId, @Param("userIds") List<String> userIds);

}
