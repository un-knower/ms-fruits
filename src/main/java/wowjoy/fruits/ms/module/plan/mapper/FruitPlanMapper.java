package wowjoy.fruits.ms.module.plan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.plan.FruitPlan;
import wowjoy.fruits.ms.module.plan.example.FruitPlanExample;

import java.util.List;


@Mapper
public interface FruitPlanMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_plan
     *
     * @mbg.generated Mon Sep 11 17:00:28 CST 2017
     */
    long countByExample(FruitPlanExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_plan
     *
     * @mbg.generated Mon Sep 11 17:00:28 CST 2017
     */
    int deleteByExample(FruitPlanExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_plan
     *
     * @mbg.generated Mon Sep 11 17:00:28 CST 2017
     */
    int insert(FruitPlan record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_plan
     *
     * @mbg.generated Mon Sep 11 17:00:28 CST 2017
     */
    int insertSelective(FruitPlan record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_plan
     *
     * @mbg.generated Mon Sep 11 17:00:28 CST 2017
     */
    List<FruitPlan> selectByExampleWithBLOBs(FruitPlanExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_plan
     *
     * @mbg.generated Mon Sep 11 17:00:28 CST 2017
     */
    List<FruitPlan> selectByExample(FruitPlanExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_plan
     *
     * @mbg.generated Mon Sep 11 17:00:28 CST 2017
     */
    int updateByExampleSelective(@Param("record") FruitPlan record, @Param("example") FruitPlanExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_plan
     *
     * @mbg.generated Mon Sep 11 17:00:28 CST 2017
     */
    int updateByExampleWithBLOBs(@Param("record") FruitPlan record, @Param("example") FruitPlanExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_plan
     *
     * @mbg.generated Mon Sep 11 17:00:28 CST 2017
     */
    int updateByExample(@Param("record") FruitPlan record, @Param("example") FruitPlanExample example);
}