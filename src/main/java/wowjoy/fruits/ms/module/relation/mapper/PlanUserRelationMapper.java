package wowjoy.fruits.ms.module.relation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.relation.entity.PlanUserRelation;
import wowjoy.fruits.ms.module.relation.example.PlanUserRelationExample;

import java.util.List;

@Mapper
public interface PlanUserRelationMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table plan_user_relation
     *
     * @mbg.generated Wed Sep 20 14:49:22 CST 2017
     */
    long countByExample(PlanUserRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table plan_user_relation
     *
     * @mbg.generated Wed Sep 20 14:49:22 CST 2017
     */
    int deleteByExample(PlanUserRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table plan_user_relation
     *
     * @mbg.generated Wed Sep 20 14:49:22 CST 2017
     */
    int insert(PlanUserRelation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table plan_user_relation
     *
     * @mbg.generated Wed Sep 20 14:49:22 CST 2017
     */
    int insertSelective(PlanUserRelation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table plan_user_relation
     *
     * @mbg.generated Wed Sep 20 14:49:22 CST 2017
     */
    List<PlanUserRelation> selectByExample(PlanUserRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table plan_user_relation
     *
     * @mbg.generated Wed Sep 20 14:49:22 CST 2017
     */
    int updateByExampleSelective(@Param("record") PlanUserRelation record, @Param("example") PlanUserRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table plan_user_relation
     *
     * @mbg.generated Wed Sep 20 14:49:22 CST 2017
     */
    int updateByExample(@Param("record") PlanUserRelation record, @Param("example") PlanUserRelationExample example);
}