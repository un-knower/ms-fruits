package wowjoy.fruits.ms.module.defect.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.defect.DefectStatusCount;
import wowjoy.fruits.ms.module.defect.DefectStatusCountExample;

import java.util.List;

@Mapper
public interface DefectStatusCountMapper extends DefectStatusCountMapperExt {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table defect_status_count
     *
     * @mbg.generated Thu May 03 13:46:05 CST 2018
     */
    long countByExample(DefectStatusCountExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table defect_status_count
     *
     * @mbg.generated Thu May 03 13:46:05 CST 2018
     */
    int deleteByExample(DefectStatusCountExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table defect_status_count
     *
     * @mbg.generated Thu May 03 13:46:05 CST 2018
     */
    int insert(DefectStatusCount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table defect_status_count
     *
     * @mbg.generated Thu May 03 13:46:05 CST 2018
     */
    int insertSelective(DefectStatusCount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table defect_status_count
     *
     * @mbg.generated Thu May 03 13:46:05 CST 2018
     */
    List<DefectStatusCount> selectByExample(DefectStatusCountExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table defect_status_count
     *
     * @mbg.generated Thu May 03 13:46:05 CST 2018
     */
    int updateByExampleSelective(@Param("record") DefectStatusCount record, @Param("example") DefectStatusCountExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table defect_status_count
     *
     * @mbg.generated Thu May 03 13:46:05 CST 2018
     */
    int updateByExample(@Param("record") DefectStatusCount record, @Param("example") DefectStatusCountExample example);
}