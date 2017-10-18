package wowjoy.fruits.ms.dao.list;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.list.FruitListDao;
import wowjoy.fruits.ms.module.list.FruitListExample;
import wowjoy.fruits.ms.module.list.mapper.FruitListMapper;

import java.util.List;

/**
 * Created by wangziwen on 2017/10/17.
 */
@Service
public class ListDaoImpl extends AbstractDaoList {
    @Autowired
    private FruitListMapper mapper;

    @Override
    protected void insert(FruitListDao dao) {
        mapper.insert(dao);
    }

    @Override
    protected void update(FruitListDao dao) {
        FruitListExample example = new FruitListExample();
        example.createCriteria().andUuidEqualTo(dao.getUuid());
        mapper.updateByExampleSelective(dao, example);
    }

    @Override
    protected List<FruitListDao> finds(FruitListDao dao) {
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
    protected void delete(FruitListDao dao) {
        FruitListExample example = new FruitListExample();
        if (StringUtils.isNotBlank(dao.getUuid()))
            example.createCriteria().andUuidEqualTo(dao.getUuid());
        if (example.createCriteria().getAllCriteria().isEmpty())
            throw new CheckException("缺少删除条件");
        mapper.deleteByExample(example);
    }

}
