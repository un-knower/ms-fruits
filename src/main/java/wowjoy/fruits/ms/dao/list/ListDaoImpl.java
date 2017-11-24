package wowjoy.fruits.ms.dao.list;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.relation.impl.ProjectListDaoImpl;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.list.FruitListDao;
import wowjoy.fruits.ms.module.list.FruitListExample;
import wowjoy.fruits.ms.module.list.mapper.FruitListMapper;
import wowjoy.fruits.ms.module.relation.entity.ProjectListRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.List;

/**
 * Created by wangziwen on 2017/10/17.
 */
@Service
@Transactional(rollbackFor = CheckException.class)
public class ListDaoImpl extends AbstractDaoList {
    @Autowired
    private FruitListMapper mapper;
    @Autowired
    private ProjectListDaoImpl listDao;

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
    public List<FruitListDao> finds(FruitListDao dao) {
        FruitListExample example = new FruitListExample();
        FruitListExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        if (StringUtils.isNotBlank(dao.getlType()))
            criteria.andLTypeEqualTo(dao.getlType());
        if (StringUtils.isNotBlank(dao.getTitle()))
            criteria.andTitleEqualTo(dao.getTitle());
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
        mapper.deleteByExample(example);
    }

    public List<FruitListDao> findByProjectId(List<String> projectIds) {
        return mapper.selectByProjectId(new FruitListExample(), projectIds);
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
    }

}
