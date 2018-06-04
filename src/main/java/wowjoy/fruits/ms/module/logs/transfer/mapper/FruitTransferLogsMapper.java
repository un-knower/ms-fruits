package wowjoy.fruits.ms.module.logs.transfer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.logs.transfer.FruitTransferLogs;
import wowjoy.fruits.ms.module.logs.transfer.FruitTransferLogsExample;

import java.util.List;

@Mapper
public interface FruitTransferLogsMapper extends FruitTransferLogsMapperExt {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruit_transfer_logs
     *
     * @mbg.generated Fri Mar 09 09:36:28 CST 2018
     */
    long countByExample(FruitTransferLogsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruit_transfer_logs
     *
     * @mbg.generated Fri Mar 09 09:36:28 CST 2018
     */
    int deleteByExample(FruitTransferLogsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruit_transfer_logs
     *
     * @mbg.generated Fri Mar 09 09:36:28 CST 2018
     */
    int insert(FruitTransferLogs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruit_transfer_logs
     *
     * @mbg.generated Fri Mar 09 09:36:28 CST 2018
     */
    int insertSelective(FruitTransferLogs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruit_transfer_logs
     *
     * @mbg.generated Fri Mar 09 09:36:28 CST 2018
     */
    List<FruitTransferLogs> selectByExample(FruitTransferLogsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruit_transfer_logs
     *
     * @mbg.generated Fri Mar 09 09:36:28 CST 2018
     */
    int updateByExampleSelective(@Param("record") FruitTransferLogs record, @Param("example") FruitTransferLogsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruit_transfer_logs
     *
     * @mbg.generated Fri Mar 09 09:36:28 CST 2018
     */
    int updateByExample(@Param("record") FruitTransferLogs record, @Param("example") FruitTransferLogsExample example);
}