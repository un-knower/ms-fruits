package wowjoy.fruits.ms.module.relation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.relation.entity.PlanProjectRelation;
import wowjoy.fruits.ms.module.relation.example.PlanProjectRelationExample;

import java.util.List;

@Mapper
public interface PlanProjectRelationMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table plan_project_relation
     *
     * @mbg.generated Fri Sep 29 13:57:28 CST 2017
     */
    long countByExample(PlanProjectRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table plan_project_relation
     *
     * @mbg.generated Fri Sep 29 13:57:28 CST 2017
     */
    int deleteByExample(PlanProjectRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table plan_project_relation
     *
     * @mbg.generated Fri Sep 29 13:57:28 CST 2017
     */
    int insert(PlanProjectRelation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table plan_project_relation
     *
     * @mbg.generated Fri Sep 29 13:57:28 CST 2017
     */
    int insertSelective(PlanProjectRelation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table plan_project_relation
     *
     * @mbg.generated Fri Sep 29 13:57:28 CST 2017
     */
    List<PlanProjectRelation> selectByExample(PlanProjectRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table plan_project_relation
     *
     * @mbg.generated Fri Sep 29 13:57:28 CST 2017
     */
    int updateByExampleSelective(@Param("record") PlanProjectRelation record, @Param("example") PlanProjectRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table plan_project_relation
     *
     * @mbg.generated Fri Sep 29 13:57:28 CST 2017
     */
    int updateByExample(@Param("record") PlanProjectRelation record, @Param("example") PlanProjectRelationExample example);
}