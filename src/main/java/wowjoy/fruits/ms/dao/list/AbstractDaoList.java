package wowjoy.fruits.ms.dao.list;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.list.FruitList;
import wowjoy.fruits.ms.module.list.FruitListDao;
import wowjoy.fruits.ms.module.list.FruitListVo;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.List;

/**
 * Created by wangziwen on 2017/10/17.
 */
public abstract class AbstractDaoList implements InterfaceDao {

    protected abstract void insert(FruitListDao dao);

    protected abstract void update(FruitListDao dao);

    protected abstract List<FruitListDao> finds(FruitListDao dao);

    protected abstract void delete(FruitListDao dao);

    /**
     * 添加任务列表
     *
     * @param vo
     */
    public void insertTask(FruitListVo vo) {
        try {
            FruitListDao dao = this.insertTemplate(vo);
            dao.setlType(FruitDict.Dict.TASK.name());
            this.insert(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("添加列表错误");
        }
    }

    /**
     * 添加模板
     *
     * @param vo
     * @return
     */
    private FruitListDao insertTemplate(FruitListVo vo) {
        FruitListDao dao = FruitList.getDao();
        dao.setUuid(vo.getUuid());
        dao.setTitle(vo.getTitle());
        dao.setDescription(vo.getDescription());
        return dao;
    }

    /**
     * 修改任务列表
     *
     * @param vo
     */
    public void update(FruitListVo vo) {
        try {
            if (!checkByUUID(vo.getUuidVo()).isNotEmpty())
                throw new CheckException("任务列表不存在，操作被拒绝");
            FruitListDao dao = FruitList.getDao();
            dao.setUuid(vo.getUuidVo());
            dao.setTitle(vo.getTitle());
            dao.setDescription(vo.getDescription());
            this.update(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("列表修改失败");
        }
    }

    /**
     * 删除任务列表
     *
     * @param vo
     */
    public void delete(FruitListVo vo) {
        try {
            FruitListDao dao = FruitList.getDao();
            dao.setUuid(vo.getUuidVo());
            this.delete(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("列表删除失败");
        }
    }

    private FruitList checkByUUID(String uuid) {
        if (StringUtils.isBlank(uuid))
            return FruitList.getEmpty();
        FruitListDao dao = FruitList.getDao();
        dao.setUuid(uuid);
        List<FruitListDao> finds = this.finds(dao);
        if (finds.isEmpty())
            return FruitList.getEmpty();
        return finds.get(0);
    }

    public List<FruitListDao> findTask(FruitListVo vo) {
        FruitListDao dao = this.findTempalte(vo);
        dao.setlType(FruitDict.Dict.TASK.name());
        return this.finds(dao);
    }

    private FruitListDao findTempalte(FruitListVo vo) {
        FruitListDao dao = FruitList.getDao();
        dao.setTitle(vo.getTitle());
        return dao;
    }
}
