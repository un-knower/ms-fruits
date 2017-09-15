package wowjoy.fruits.ms.dao.team;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.team.FruitTeam;
import wowjoy.fruits.ms.module.team.FruitTeamDao;
import wowjoy.fruits.ms.module.team.FruitTeamVo;

import java.util.List;

/**
 * Created by wangziwen on 2017/9/6.
 */
public abstract class AbstractDaoTeam implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    protected abstract List<FruitTeam> finds(FruitTeamDao dao);


    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/
    public List<FruitTeam> finds(FruitTeamVo vo) {
        final FruitTeamDao dao = FruitTeam.getFruitTeamDao();
        dao.setUuid(vo.getUuidVo());
        dao.setTitle(vo.getTitle());
        return this.finds(dao);
    }

    /*******************************************************
     * 仅用于实现类。内部类尽量采用static，非静态类不利于垃圾回收。 *
     *******************************************************/
    protected static class CheckTeamException extends CheckException {
        public CheckTeamException(String message) {
            super("【Project exception】" + message);
        }
    }

}
