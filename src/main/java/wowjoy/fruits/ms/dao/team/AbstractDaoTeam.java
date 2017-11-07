package wowjoy.fruits.ms.dao.team;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.ServiceException;
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

    protected abstract List<FruitTeamDao> finds(FruitTeamDao dao);

    protected abstract List<FruitTeamDao> findRelation(FruitTeamDao dao);

    protected abstract void insert(FruitTeamDao dao);

    protected abstract void update(FruitTeamDao data);

    protected abstract void delete(FruitTeamDao data);

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/
    public final List<FruitTeamDao> finds(FruitTeamVo vo) {
        final FruitTeamDao dao = FruitTeam.getDao();
        dao.setUuid(vo.getUuidVo());
        dao.setTitle(vo.getTitle());
        return this.finds(dao);
    }

    public final List<FruitTeamDao> findRelation(FruitTeamVo vo) {
        FruitTeamDao dao = FruitTeam.getDao();
        dao.setTitle(vo.getTitle());
        dao.setUuid(vo.getUuidVo());
        return this.findRelation(dao);
    }

    public final void insert(FruitTeamVo vo) {
        try {
            FruitTeamDao dao = FruitTeam.getDao();
            dao.setUuid(vo.getUuid());
            dao.setTitle(vo.getTitle());
            dao.setDescription(vo.getDescription());
            dao.setInUsers(vo.getInUsers());

            this.insert(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("团队添加数据错误");
        }
    }

    public final void update(FruitTeamVo vo) {
        try {
            if (StringUtils.isBlank(vo.getUuidVo()))
                throw new CheckException("Team id not is null");
            FruitTeamDao dao = FruitTeam.getDao();
            dao.setUuid(vo.getUuidVo());
            dao.setInUsers(vo.getInUsers());
            dao.setTitle(vo.getTitle());
            dao.setDescription(vo.getDescription());
            this.update(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("团队修改[" + vo.getUuidVo() + "]错误");
        }

    }

    public final void delete(String uuid) {
        try {
            if (StringUtils.isBlank(uuid))
                throw new CheckException("Team id not is null");
            FruitTeamDao dao = FruitTeam.getDao();
            dao.setUuid(uuid);
            this.delete(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("团队删除[" + uuid + "]错误");
        }

    }

}
