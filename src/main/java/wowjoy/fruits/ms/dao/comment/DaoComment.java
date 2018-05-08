package wowjoy.fruits.ms.dao.comment;

import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.module.comment.DefectComment;
import wowjoy.fruits.ms.module.comment.FruitComment;
import wowjoy.fruits.ms.module.comment.mapper.FruitCommentMapper;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.user.mapper.FruitUserMapper;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
@Service
@Transactional
public class DaoComment extends ServiceComment {
    private final FruitCommentMapper commentMapper;
    private final FruitUserMapper fruitUserMapper;

    @Autowired
    public DaoComment(FruitCommentMapper commentMapper, FruitUserMapper fruitUserMapper) {
        this.commentMapper = commentMapper;
        this.fruitUserMapper = fruitUserMapper;
    }

    @Override
    public void insert(FruitComment.Insert comment) {
        commentMapper.insertSelective(comment);
    }

    /*查询所有缺陷关联评论*/
    @Override
    public ArrayList<DefectComment> findDefect(String defectId) {
        return commentMapper.selectByDefectId(defectId);
    }

    @Override
    ArrayList<FruitUserDao> findUserByUserId(ArrayList<String> userIds) {
        return Optional.ofNullable(userIds)
                .filter(ids -> !ids.isEmpty())
                .map(ids -> {
                    FruitUserExample example = new FruitUserExample();
                    example.createCriteria().andUserIdIn(ids);
                    return fruitUserMapper.selectByExample(example);
                }).map(Lists::newArrayList)
                .orElseGet(ArrayList::new);
    }

}
