package wowjoy.fruits.ms.dao.list;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.impl.ProjectListDaoImpl;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.list.FruitList;
import wowjoy.fruits.ms.module.list.FruitListDao;
import wowjoy.fruits.ms.module.list.FruitListExample;
import wowjoy.fruits.ms.module.list.mapper.FruitListMapper;
import wowjoy.fruits.ms.module.relation.entity.ProjectListRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by wangziwen on 2017/10/17.
 */
@Service
@Transactional(rollbackFor = CheckException.class)
public class ListDaoImpl extends AbstractDaoList {
    private final FruitListMapper mapper;
    private final ProjectListDaoImpl listDao;
    private final static Consumer<FruitListExample> afterExample = (listExample) -> {
        if (listExample.getOredCriteria().isEmpty())
            listExample.getOredCriteria().forEach((criteria) -> criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name()));
        else
            listExample.createCriteria().andIsDeletedEqualTo(FruitDict.Systems.N.name());
    };

    @Autowired
    public ListDaoImpl(FruitListMapper mapper, ProjectListDaoImpl listDao) {
        this.mapper = mapper;
        this.listDao = listDao;
    }

    /**
     * 添加列表，并维护关联列表
     *
     * @param dao
     */
    @Override
    public void insert(FruitListDao dao) {
        mapper.insertSelective(dao);
        Relation.newProject(listDao, dao).insertProject();
    }

    @Override
    public void update(FruitListDao dao) {
        FruitListExample example = new FruitListExample();
        example.createCriteria().andUuidEqualTo(dao.getUuid());
        mapper.updateByExampleSelective(dao, example);
    }

    @Override
    public List<FruitList> finds(Consumer<FruitListExample> exampleConsumer) {
        FruitListExample example = new FruitListExample();
        exampleConsumer.accept(example);
        return mapper.selectByExample(example);
    }

    @Override
    public void delete(FruitListDao dao) {
        FruitListExample example = new FruitListExample();
        FruitListExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckException("缺少删除条件");
        FruitListDao delete = FruitList.getDao();
        delete.setIsDeleted(FruitDict.Systems.Y.name());
        mapper.updateByExampleSelective(delete, example);
        Relation.newProject(listDao, dao).removeProjects();
    }

    public List<FruitList> findByProjectId(String projectId, Consumer<FruitListExample> unaryOperator) {
        FruitListExample example = new FruitListExample();
        unaryOperator.accept(example);
        afterExample.accept(example);
        return mapper.selectByProjectId(example, projectId);
    }

    private static class Relation {
        private final ProjectListDaoImpl projectListDao;
        private final FruitListDao dao;

        private Relation(ProjectListDaoImpl projectListDao, FruitListDao dao) {
            this.projectListDao = projectListDao;
            this.dao = dao;
        }


        public static Relation newProject(ProjectListDaoImpl projectListDao, FruitListDao dao) {
            return new Relation(projectListDao, dao);
        }

        public void insertProject() {
            this.dao.getProjectRelation(FruitDict.Systems.ADD).forEach((i) -> {
                projectListDao.insert(ProjectListRelation.newInstance(i, dao.getUuid()));
            });
        }

        /*删除所有项目关联信息*/
        public void removeProjects() {
            projectListDao.deleted(ProjectListRelation.newInstance(null, dao.getUuid()));
        }
    }

}
