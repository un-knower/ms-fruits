package wowjoy.fruits.ms.dao.list;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.list.FruitList;
import wowjoy.fruits.ms.module.list.FruitListDao;
import wowjoy.fruits.ms.module.list.FruitListExample;
import wowjoy.fruits.ms.module.list.FruitListVo;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by wangziwen on 2017/10/17.
 */
public abstract class AbstractDaoList implements InterfaceDao {

    protected abstract void insert(FruitListDao dao);

    protected abstract void update(FruitListDao dao);

    protected abstract List<FruitList> finds(Consumer<FruitListExample> exampleConsumer);

    protected abstract void delete(FruitListDao dao);

    protected abstract List<FruitList> findByProjectId(String projectId, Consumer<FruitListExample> unaryOperator);

    public final void insertProject(FruitListVo vo) {
        try {
            FruitListDao dao = insertTemplate(vo);
            dao.setProjectRelation(vo.getProjectRelation());
            dao.setlType(FruitDict.Parents.PROJECT.name());
            if (dao.getProjectRelation(FruitDict.Systems.ADD).isEmpty())
                throw new CheckException("添加项目列表时，必须绑定项目id");
            this.insert(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new CheckException("项目列表添加失败");
        }
    }

    /**
     * 添加模板
     *
     * @param vo
     * @return
     */
    private FruitListDao insertTemplate(FruitListVo vo) {
        if (StringUtils.isBlank(vo.getTitle()))
            throw new CheckException("列表标题不能为空");
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
    public final void update(FruitListVo vo) {
        try {
            if (!checkByUUID(vo.getUuidVo()).isPresent())
                throw new CheckException("列表不存在，操作被拒绝");
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
     * 删除列表
     *
     * @param vo
     */
    public final void delete(FruitListVo vo) {
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

    private Optional<FruitList> checkByUUID(String uuid) {
        if (StringUtils.isBlank(uuid))
            return Optional.empty();
        FruitListDao dao = FruitList.getDao();
        dao.setUuid(uuid);
        return this.finds(listExample -> listExample.createCriteria().andUuidEqualTo(uuid).andIsDeletedEqualTo(FruitDict.Systems.N.name())).stream().findAny();
    }

}
