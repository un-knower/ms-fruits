package wowjoy.fruits.ms.module.task.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.list.FruitListExample;
import wowjoy.fruits.ms.module.plan.FruitPlanTask;
import wowjoy.fruits.ms.module.task.*;

import java.util.List;

/**
 * Created by wangziwen on 2017/8/31.
 */
@Mapper
public interface FruitTaskMapperExt {
    List<FruitTaskInfo> selectByListExampleAndProjectId(@Param("example") FruitTaskExample example, @Param("listExample") FruitListExample listExample, @Param("projectId") String projectId);

    List<FruitTaskUser> selectJoinUserByTaskIds(@Param("ids") List<String> example);

    List<FruitTaskProject> selectProjectByTask(@Param("example") FruitTaskExample example, @Param("taskIds") List<String> taskIds);

    List<FruitTaskList> selectListByTask(@Param("example") FruitTaskExample example);

    List<FruitTaskUser> findUserByTaskExampleAndUserIdOrProjectId(@Param("example") FruitTaskExample example, @Param("userIds") List<String> userIds, @Param("projectId") String projectId);

    List<FruitTaskInfo> selectByExampleExt(FruitTask.Search search);

    List<FruitPlanTask> selectTaskByPlanIds(@Param("example") FruitTaskExample taskExample, @Param("planIds") List<String> planIds);

    Integer taskCountByListId(@Param("listId") String listId);

    /************************************************************************************************
     *                                       个人中心专供                                            *
     ************************************************************************************************/
    List<FruitTaskInfo> myTaskByExample(@Param("example") FruitTaskExample example, @Param("userIds") List<String> userIds, @Param("projectId") String projectId);

    List<FruitTaskInfo> myCreateTask(@Param("example") FruitTaskExample example, @Param("userId") String userId, @Param("projectId") String projectId);

    List<FruitTaskProject> myCreateTaskFromProjects(@Param("userId") String userId);

}
