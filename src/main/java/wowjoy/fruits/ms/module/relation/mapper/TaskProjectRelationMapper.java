package wowjoy.fruits.ms.module.relation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.relation.entity.TaskProjectRelation;
import wowjoy.fruits.ms.module.relation.example.TaskProjectRelationExample;

import java.util.List;

@Mapper
public interface TaskProjectRelationMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_project_relation
     *
     * @mbg.generated Mon Oct 09 11:06:45 CST 2017
     */
    long countByExample(TaskProjectRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_project_relation
     *
     * @mbg.generated Mon Oct 09 11:06:45 CST 2017
     */
    int deleteByExample(TaskProjectRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_project_relation
     *
     * @mbg.generated Mon Oct 09 11:06:45 CST 2017
     */
    int insert(TaskProjectRelation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_project_relation
     *
     * @mbg.generated Mon Oct 09 11:06:45 CST 2017
     */
    int insertSelective(TaskProjectRelation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_project_relation
     *
     * @mbg.generated Mon Oct 09 11:06:45 CST 2017
     */
    List<TaskProjectRelation> selectByExample(TaskProjectRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_project_relation
     *
     * @mbg.generated Mon Oct 09 11:06:45 CST 2017
     */
    int updateByExampleSelective(@Param("record") TaskProjectRelation record, @Param("example") TaskProjectRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_project_relation
     *
     * @mbg.generated Mon Oct 09 11:06:45 CST 2017
     */
    int updateByExample(@Param("record") TaskProjectRelation record, @Param("example") TaskProjectRelationExample example);
}