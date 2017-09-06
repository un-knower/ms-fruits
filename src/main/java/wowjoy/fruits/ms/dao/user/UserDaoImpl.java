package wowjoy.fruits.ms.dao.user;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserEmpty;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.user.mapper.FruitUserMapper;

import java.util.List;

/**
 * Created by wangziwen on 2017/8/31.
 */
@Service
public class UserDaoImpl extends AbstractDaoUser {

    @Autowired
    private FruitUserMapper userMapper;

    @Override
    public void insert(FruitUser... user) {
        userMapper.inserts(user);
    }

    @Override
    protected FruitUser findByUser() {
        final FruitUserExample example = new FruitUserExample();
        final FruitUserExample.Criteria criteria = example.createCriteria();
        if (this.getFruitUser().isNotEmpty()) {
            if (StringUtils.isNotBlank(this.getFruitUser().getUserId()))
                criteria.andUserIdEqualTo(this.getFruitUser().getUserId());
            if (StringUtils.isNotBlank(this.getFruitUser().getUserName()))
                criteria.andUserNameEqualTo(this.getFruitUser().getUserName());
            if (StringUtils.isNotBlank(this.getFruitUser().getUserEmail()))
                criteria.andUserEmailEqualTo(this.getFruitUser().getUserEmail());
        }
        final List<FruitUser> result = userMapper.selectByExample(example);
        if (result.isEmpty() || result.size() > 1)
            return FruitUserEmpty.getInstance("用户不存在");
        return result.get(0);
    }

}
