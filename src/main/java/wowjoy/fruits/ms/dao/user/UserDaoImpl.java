package wowjoy.fruits.ms.dao.user;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.user.mapper.FruitUserMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by wangziwen on 2017/8/31.
 */
@Transactional
@Service
public class UserDaoImpl extends AbstractDaoUser {

    @Autowired
    private FruitUserMapper mapper;

    @Override
    public void insert(FruitUser... user) {
        mapper.inserts(user);
    }

    @Override
    public List<FruitUserDao> finds(FruitUserDao dao) {
        final FruitUserExample example = new FruitUserExample();
        final FruitUserExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getUserName()))
            criteria.andUserNameLike(MessageFormat.format("%{0}%", dao.getUserName()));
        criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name());
        return mapper.selectByExample(example);
    }

    @Override
    public void clearByUserId(String... ids) {
        FruitUserExample example = new FruitUserExample();
        example.createCriteria().andUserIdIn(Arrays.asList(ids));
        mapper.deleteByExample(example);
        FruitUserDao dao = FruitUser.getDao();
        dao.setIsDeleted(FruitDict.Systems.Y.name());
        mapper.updateByExampleSelective(dao, new FruitUserExample());
    }

    @Override
    public List<FruitUserDao> findByAccount(FruitUserDao dao) {
        FruitUserExample example = new FruitUserExample();
        example.createCriteria().andIsDeletedEqualTo(FruitDict.Systems.N.name());
        return mapper.selectByAccount(example, dao.getPrincipal());
    }

    public List<FruitUserDao> findExample(Consumer<FruitUserExample> exampleConsumer) {
        FruitUserExample example = new FruitUserExample();
        exampleConsumer.accept(example);
        return mapper.selectByExample(example);
    }

}
