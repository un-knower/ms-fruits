package wowjoy.fruits.ms.dao.user;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.user.mapper.FruitUserMapper;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by wangziwen on 2017/8/31.
 */
@Service
public class UserDaoImpl extends AbstractDaoUser {

    @Autowired
    private FruitUserMapper mapper;

    @Override
    public void insert(FruitUser... user) {
        mapper.inserts(user);
    }

    @Override
    protected List<FruitUser> finds(FruitUserDao dao) {
        final FruitUserExample example = new FruitUserExample();
        final FruitUserExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getUserEmail()))
            criteria.andUserNameLike(MessageFormat.format("%{0}%", dao.getUserEmail()));
        if (StringUtils.isNotBlank(dao.getUserName()))
            criteria.andUserNameLike(MessageFormat.format("%{0}%", dao.getUserName()));
        return mapper.selectByExample(example);
    }

}
