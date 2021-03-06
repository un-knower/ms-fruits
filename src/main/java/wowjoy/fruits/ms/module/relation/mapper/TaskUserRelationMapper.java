package wowjoy.fruits.ms.module.relation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.relation.entity.TaskUserRelation;
import wowjoy.fruits.ms.module.relation.example.TaskUserRelationExample;

import java.util.List;

@Mapper
public interface TaskUserRelationMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_user_relation
     *
     * @mbg.generated Wed Oct 11 13:55:22 CST 2017
     */
    long countByExample(TaskUserRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_user_relation
     *
     * @mbg.generated Wed Oct 11 13:55:22 CST 2017
     */
    int deleteByExample(TaskUserRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_user_relation
     *
     * @mbg.generated Wed Oct 11 13:55:22 CST 2017
     */
    int insert(TaskUserRelation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_user_relation
     *
     * @mbg.generated Wed Oct 11 13:55:22 CST 2017
     */
    int insertSelective(TaskUserRelation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_user_relation
     *
     * @mbg.generated Wed Oct 11 13:55:22 CST 2017
     */
    List<TaskUserRelation> selectByExample(TaskUserRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_user_relation
     *
     * @mbg.generated Wed Oct 11 13:55:22 CST 2017
     */
    int updateByExampleSelective(@Param("record") TaskUserRelation record, @Param("example") TaskUserRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_user_relation
     *
     * @mbg.generated Wed Oct 11 13:55:22 CST 2017
     */
    int updateByExample(@Param("record") TaskUserRelation record, @Param("example") TaskUserRelationExample example);
}