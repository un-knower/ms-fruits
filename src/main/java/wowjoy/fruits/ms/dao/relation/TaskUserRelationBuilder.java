package wowjoy.fruits.ms.dao.relation;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.module.task.example.TaskUserRelationExample;
import wowjoy.fruits.ms.module.task.mapper.TaskUserRelationMapper;
import wowjoy.fruits.ms.module.task.relation.TaskUserRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

/**
 * Created by wangziwen on 2017/8/30.
 */
public class TaskUserRelationBuilder extends RelationBuilder {
    private final Class<TaskUserRelationMapper> mapperClass = TaskUserRelationMapper.class;

    @Override
    public TaskUserRelation getAbstractEntity() {
        final TaskUserRelation result = (TaskUserRelation) super.getAbstractEntity();
        if (StringUtils.isBlank(result.getUserId()) || StringUtils.isBlank(result.getTaskId()))
            throw new RelationBuilderException("关联用户id不用为空");
        return result;
    }

    @Override
    void insert() {
        super.getContext(mapperClass).insertSelective(this.getAbstractEntity());
    }

    @Override
    void update() {
    }

    @Override
    void deleted() {
        final TaskUserRelationExample example = new TaskUserRelationExample();
        example.createCriteria().andUserIdEqualTo(this.getAbstractEntity().getUserId())
                .andTaskIdEqualTo(this.getAbstractEntity().getTaskId())
                .andIsDeletedEqualTo(FruitDict.Dict.N.name());
        this.getAbstractEntity().setIsDeleted(FruitDict.Dict.Y.name());
        super.getContext(mapperClass).updateByExampleSelective(this.getAbstractEntity(),example);
    }
}
