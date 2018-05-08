package wowjoy.fruits.ms.module.defect.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.defect.FruitDefect;
import wowjoy.fruits.ms.module.defect.FruitDefectExample;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface FruitDefectMapper extends FruitDefectMapperExt {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_defect
     *
     * @mbg.generated Fri May 04 11:04:39 CST 2018
     */
    long countByExample(FruitDefectExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_defect
     *
     * @mbg.generated Fri May 04 11:04:39 CST 2018
     */
    int deleteByExample(FruitDefectExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_defect
     *
     * @mbg.generated Fri May 04 11:04:39 CST 2018
     */
    int insert(FruitDefect record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_defect
     *
     * @mbg.generated Fri May 04 11:04:39 CST 2018
     */
    int insertSelective(FruitDefect record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_defect
     *
     * @mbg.generated Fri May 04 11:04:39 CST 2018
     */
    List<FruitDefect> selectByExampleWithBLOBs(FruitDefectExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_defect
     *
     * @mbg.generated Fri May 04 11:04:39 CST 2018
     */
    ArrayList<FruitDefect> selectByExample(FruitDefectExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_defect
     *
     * @mbg.generated Fri May 04 11:04:39 CST 2018
     */
    int updateByExampleSelective(@Param("record") FruitDefect record, @Param("example") FruitDefectExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_defect
     *
     * @mbg.generated Fri May 04 11:04:39 CST 2018
     */
    int updateByExampleWithBLOBs(@Param("record") FruitDefect record, @Param("example") FruitDefectExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_defect
     *
     * @mbg.generated Fri May 04 11:04:39 CST 2018
     */
    int updateByExample(@Param("record") FruitDefect record, @Param("example") FruitDefectExample example);
}