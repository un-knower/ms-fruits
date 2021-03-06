package wowjoy.fruits.ms.module.user.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;

import java.util.List;

public interface FruitUserMapper extends FruitUserMapperExt {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_user
     *
     * @mbg.generated Thu Mar 22 15:02:56 CST 2018
     */
    long countByExample(FruitUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_user
     *
     * @mbg.generated Thu Mar 22 15:02:56 CST 2018
     */
    int deleteByExample(FruitUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_user
     *
     * @mbg.generated Thu Mar 22 15:02:56 CST 2018
     */
    int insert(FruitUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_user
     *
     * @mbg.generated Thu Mar 22 15:02:56 CST 2018
     */
    int insertSelective(FruitUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_user
     *
     * @mbg.generated Thu Mar 22 15:02:56 CST 2018
     */
    List<FruitUserDao> selectByExample(FruitUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_user
     *
     * @mbg.generated Thu Mar 22 15:02:56 CST 2018
     */
    int updateByExampleSelective(@Param("record") FruitUser record, @Param("example") FruitUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_user
     *
     * @mbg.generated Thu Mar 22 15:02:56 CST 2018
     */
    int updateByExample(@Param("record") FruitUser record, @Param("example") FruitUserExample example);
}