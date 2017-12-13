package wowjoy.fruits.ms.module.notepad.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.notepad.FruitNotepad;
import wowjoy.fruits.ms.module.notepad.FruitNotepadDao;
import wowjoy.fruits.ms.module.notepad.FruitNotepadExample;

import java.util.List;

public interface FruitNotepadMapper extends FruitNotepadMapperExt {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_notepad
     *
     * @mbg.generated Wed Dec 13 14:51:43 CST 2017
     */
    long countByExample(FruitNotepadExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_notepad
     *
     * @mbg.generated Wed Dec 13 14:51:43 CST 2017
     */
    int deleteByExample(FruitNotepadExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_notepad
     *
     * @mbg.generated Wed Dec 13 14:51:43 CST 2017
     */
    int insert(FruitNotepad record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_notepad
     *
     * @mbg.generated Wed Dec 13 14:51:43 CST 2017
     */
    int insertSelective(FruitNotepad record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_notepad
     *
     * @mbg.generated Wed Dec 13 14:51:43 CST 2017
     */
    List<FruitNotepadDao> selectByExample(FruitNotepadExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_notepad
     *
     * @mbg.generated Wed Dec 13 14:51:43 CST 2017
     */
    int updateByExampleSelective(@Param("record") FruitNotepad record, @Param("example") FruitNotepadExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_notepad
     *
     * @mbg.generated Wed Dec 13 14:51:43 CST 2017
     */
    int updateByExample(@Param("record") FruitNotepad record, @Param("example") FruitNotepadExample example);
}