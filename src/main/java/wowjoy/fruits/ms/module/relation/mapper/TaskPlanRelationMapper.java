package wowjoy.fruits.ms.module.relation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.relation.entity.TaskPlanRelation;
import wowjoy.fruits.ms.module.relation.example.TaskPlanRelationExample;

import java.util.List;

@Mapper
public interface TaskPlanRelationMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_plan_relation
     *
     * @mbg.generated Mon Oct 09 11:06:45 CST 2017
     */
    long countByExample(TaskPlanRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_plan_relation
     *
     * @mbg.generated Mon Oct 09 11:06:45 CST 2017
     */
    int deleteByExample(TaskPlanRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_plan_relation
     *
     * @mbg.generated Mon Oct 09 11:06:45 CST 2017
     */
    int insert(TaskPlanRelation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_plan_relation
     *
     * @mbg.generated Mon Oct 09 11:06:45 CST 2017
     */
    int insertSelective(TaskPlanRelation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_plan_relation
     *
     * @mbg.generated Mon Oct 09 11:06:45 CST 2017
     */
    List<TaskPlanRelation> selectByExample(TaskPlanRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_plan_relation
     *
     * @mbg.generated Mon Oct 09 11:06:45 CST 2017
     */
    int updateByExampleSelective(@Param("record") TaskPlanRelation record, @Param("example") TaskPlanRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_plan_relation
     *
     * @mbg.generated Mon Oct 09 11:06:45 CST 2017
     */
    int updateByExample(@Param("record") TaskPlanRelation record, @Param("example") TaskPlanRelationExample example);
}