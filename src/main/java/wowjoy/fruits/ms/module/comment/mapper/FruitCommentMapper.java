package wowjoy.fruits.ms.module.comment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.comment.FruitComment;
import wowjoy.fruits.ms.module.comment.FruitCommentExample;

import java.util.List;
@Mapper
public interface FruitCommentMapper extends FruitCommentMapperExt{
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruit_comment
     *
     * @mbg.generated Mon May 07 09:59:26 CST 2018
     */
    long countByExample(FruitCommentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruit_comment
     *
     * @mbg.generated Mon May 07 09:59:26 CST 2018
     */
    int deleteByExample(FruitCommentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruit_comment
     *
     * @mbg.generated Mon May 07 09:59:26 CST 2018
     */
    int insert(FruitComment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruit_comment
     *
     * @mbg.generated Mon May 07 09:59:26 CST 2018
     */
    int insertSelective(FruitComment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruit_comment
     *
     * @mbg.generated Mon May 07 09:59:26 CST 2018
     */
    List<FruitComment> selectByExample(FruitCommentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruit_comment
     *
     * @mbg.generated Mon May 07 09:59:26 CST 2018
     */
    int updateByExampleSelective(@Param("record") FruitComment record, @Param("example") FruitCommentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruit_comment
     *
     * @mbg.generated Mon May 07 09:59:26 CST 2018
     */
    int updateByExample(@Param("record") FruitComment record, @Param("example") FruitCommentExample example);
}