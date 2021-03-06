package wowjoy.fruits.ms.module.project.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.project.FruitProjectDao;
import wowjoy.fruits.ms.module.project.FruitProjectExample;

import java.util.List;

public interface FruitProjectMapper extends FruitProjectMapperExt {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Wed Feb 07 13:55:18 CST 2018
     */
    long countByExample(FruitProjectExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Wed Feb 07 13:55:18 CST 2018
     */
    int deleteByExample(FruitProjectExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Wed Feb 07 13:55:18 CST 2018
     */
    int insert(FruitProject record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Wed Feb 07 13:55:18 CST 2018
     */
    int insertSelective(FruitProject record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Wed Feb 07 13:55:18 CST 2018
     */
    List<FruitProject> selectByExampleWithBLOBs(FruitProjectExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Wed Feb 07 13:55:18 CST 2018
     */
    List<FruitProject> selectByExample(FruitProjectExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Wed Feb 07 13:55:18 CST 2018
     */
    int updateByExampleSelective(@Param("record") FruitProject record, @Param("example") FruitProjectExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Wed Feb 07 13:55:18 CST 2018
     */
    int updateByExampleWithBLOBs(@Param("record") FruitProject record, @Param("example") FruitProjectExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Wed Feb 07 13:55:18 CST 2018
     */
    int updateByExample(@Param("record") FruitProject record, @Param("example") FruitProjectExample example);
}