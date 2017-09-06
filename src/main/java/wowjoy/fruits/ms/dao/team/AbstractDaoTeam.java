package wowjoy.fruits.ms.dao.team;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.dao.relation.AbstractDaoRelation;
import wowjoy.fruits.ms.dao.relation.UserTeamDaoImpl;
import wowjoy.fruits.ms.module.relation.entity.UserTeamRelation;
import wowjoy.fruits.ms.module.team.FruitTeam;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.util.List;

/**
 * Created by wangziwen on 2017/9/5.
 */
public abstract class AbstractDaoTeam implements InterfaceDao {

    protected abstract void insert();

    protected abstract List<FruitTeam> findByTeam();

    private FruitTeam fruitTeam;

    private AbstractDaoRelation getUserTeamRelation() {
        return ApplicationContextUtils.getContext().getBean(UserTeamDaoImpl.class);
    }

    protected FruitTeam getFruitTeam() {
        return fruitTeam;
    }

    private AbstractDaoTeam setFruitTeam(FruitTeam fruitTeam) {
        this.fruitTeam = fruitTeam;
        return this;
    }

    public List<FruitTeam> findByTeam(FruitTeam team) {
        final List<FruitTeam> result = this.setFruitTeam(team).findByTeam();
        result.forEach((i) -> {
            i.setUsers(this.getUserTeamRelation()
                    .setAbstractEntity(new UserTeamRelation("", i.getUuid(), ""))
                    .findByEntity());
        });
        return result;
    }
}
