package wowjoy.fruits.ms.dao.comment;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.module.comment.DefectComment;
import wowjoy.fruits.ms.module.comment.FruitComment;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.stream.Collectors.*;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public abstract class ServiceComment {
    public abstract void insert(FruitComment.Insert comment);

    /*查询所有缺陷关联评论*/
    public abstract ArrayList<DefectComment> findDefect(String defectId);

    abstract ArrayList<FruitUserDao> findUserByUserId(ArrayList<String> userIds);

    public <T extends FruitComment.Info> ArrayList<T> joinUser(Supplier<ArrayList<T>> commentSupplier) {
        ArrayList<T> intoComments = commentSupplier.get();
        Optional.ofNullable(intoComments)
                .map(comments -> comments.stream().map(FruitComment::getUserId).collect(toCollection(ArrayList::new)))
                .map(this::findUserByUserId)
                .map(users -> users.stream().collect(toMap(FruitUser::getUserId, user -> user)))
                .ifPresent(mapUser -> intoComments.forEach(comment -> Optional.ofNullable(mapUser.get(comment.getUserId()))
                        .ifPresent(comment::setUser)));
        return intoComments;
    }

    public <T extends FruitComment.Info> ArrayList<T> commentTree(ArrayList<T> intoComment) {
        Map<Boolean, ArrayList<T>> booleMap = intoComment.stream().collect(partitioningBy(comment -> StringUtils.isBlank(comment.getParentId()), toCollection(ArrayList::new)));
        return loopComment(booleMap.get(true), booleMap.get(false).stream().collect(groupingBy(FruitComment::getParentId, toCollection(ArrayList::new))));
    }

    private <T extends FruitComment.Info> ArrayList<T> loopComment(ArrayList<T> baseComment, Map<String, ArrayList<T>> intoComment) {
        baseComment.forEach(comment -> Optional.ofNullable(intoComment.get(comment.getUuid()))
                .map(comments -> loopComment(comments, intoComment))
                .ifPresent(comment::setComments));
        return baseComment;
    }
}
