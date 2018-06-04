package wowjoy.fruits.ms.dao.list;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.list.FruitList;
import wowjoy.fruits.ms.module.list.FruitListDao;
import wowjoy.fruits.ms.module.list.FruitListExample;
import wowjoy.fruits.ms.module.list.FruitListVo;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Exception.Check;

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

    public abstract void delete(String listId);

    public abstract int findTaskCountByListId(String listId);

    protected abstract List<FruitList> findByProjectId(String projectId, Consumer<FruitListExample> unaryOperator);

    public final void insertProject(FruitListVo vo) {
        FruitListDao dao = insertTemplate(vo);
        dao.setProjectRelation(vo.getProjectRelation());
        dao.setlType(FruitDict.Parents.PROJECT.name());
        if (dao.getProjectRelation(FruitDict.Systems.ADD).isEmpty())
            throw new CheckException(Check.LIST_PROJECT_NULL.name());
        this.insert(dao);
    }

    /**
     * 添加模板
     *
     * @param vo
     * @return
     */
    private FruitListDao insertTemplate(FruitListVo vo) {
        if (StringUtils.isBlank(vo.getTitle()))
            throw new CheckException(Check.LIST_TITLE_NULL.name());
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
        if (!checkByUUID(vo.getUuidVo()).isPresent())
            throw new CheckException(Check.SYSTEM_NULL.name());
        FruitListDao dao = FruitList.getDao();
        dao.setUuid(vo.getUuidVo());
        dao.setTitle(vo.getTitle());
        dao.setDescription(vo.getDescription());
        this.update(dao);
    }

    /**
     * 删除列表
     *
     * @param listId
     */
    public final void beforeDelete(String listId) {
        Optional.ofNullable(listId)
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException(Check.SYSTEM_NULL.name()));
        Optional.of(this.findTaskCountByListId(listId))
                .filter(count -> count == 0)
                .orElseThrow(() -> new CheckException(Check.LIST_EXISTS_TASK.name()));
        this.delete(listId);
    }

    private Optional<FruitList> checkByUUID(String uuid) {
        if (StringUtils.isBlank(uuid))
            return Optional.empty();
        FruitListDao dao = FruitList.getDao();
        dao.setUuid(uuid);
        return this.finds(listExample -> listExample.createCriteria().andUuidEqualTo(uuid).andIsDeletedEqualTo(FruitDict.Systems.N.name())).stream().findAny();
    }

}
