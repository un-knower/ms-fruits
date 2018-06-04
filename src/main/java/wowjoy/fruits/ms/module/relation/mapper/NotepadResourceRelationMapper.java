package wowjoy.fruits.ms.module.relation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.relation.entity.NotepadResourceRelation;
import wowjoy.fruits.ms.module.relation.example.NotepadResourceRelationExample;

import java.util.List;

@Mapper
public interface NotepadResourceRelationMapper extends NotepadResourceRelationMapperExt {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table notepad_resource_relation
     *
     * @mbg.generated Wed May 09 10:26:30 CST 2018
     */
    long countByExample(NotepadResourceRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table notepad_resource_relation
     *
     * @mbg.generated Wed May 09 10:26:30 CST 2018
     */
    int deleteByExample(NotepadResourceRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table notepad_resource_relation
     *
     * @mbg.generated Wed May 09 10:26:30 CST 2018
     */
    int insert(NotepadResourceRelation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table notepad_resource_relation
     *
     * @mbg.generated Wed May 09 10:26:30 CST 2018
     */
    int insertSelective(NotepadResourceRelation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table notepad_resource_relation
     *
     * @mbg.generated Wed May 09 10:26:30 CST 2018
     */
    List<NotepadResourceRelation> selectByExample(NotepadResourceRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table notepad_resource_relation
     *
     * @mbg.generated Wed May 09 10:26:30 CST 2018
     */
    int updateByExampleSelective(@Param("record") NotepadResourceRelation record, @Param("example") NotepadResourceRelationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table notepad_resource_relation
     *
     * @mbg.generated Wed May 09 10:26:30 CST 2018
     */
    int updateByExample(@Param("record") NotepadResourceRelation record, @Param("example") NotepadResourceRelationExample example);
}