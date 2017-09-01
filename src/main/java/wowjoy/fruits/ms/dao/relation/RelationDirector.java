package wowjoy.fruits.ms.dao.relation;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by wangziwen on 2017/8/30.
 */
public class RelationDirector {
    private List<RelationBuilder> builder;

    public RelationDirector(RelationBuilder builder) {
        this.addBuilder(builder);
    }

    public void addBuilder(RelationBuilder builder) {
        this.getBuilder().add(builder);
    }

    public List<RelationBuilder> getBuilder() {
        if (this.builder == null)
            this.builder = Lists.newLinkedList();
        return builder;
    }

    public static RelationDirector getInstance(RelationBuilder builder) {
        return new RelationDirector(builder);
    }

    public void insert() {
        this.getBuilder().forEach((i) -> {
            i.insert();
        });
    }

    public void update() {
        this.getBuilder().forEach((i) -> {
            i.update();
        });
    }

    public void deleted() {
        this.getBuilder().forEach((i) -> {
            i.deleted();
        });
    }


}
