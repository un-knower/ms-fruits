package wowjoy.fruits.ms.dao.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.mapper.FruitUserMapper;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by wangziwen on 2017/8/31.
 */
@Service
public class UserDaoImpl extends AbstractDaoUser {

    @Autowired
    private FruitUserMapper userMapper;
    @Autowired
    private HRDataParset hrDataParset;

    @Override
    public void insert(FruitUser... user) {
        userMapper.inserts(user);
    }

    @Override
    public LinkedList<FruitUser> build() {
        insert(hrDataParset.build().getList().toArray(new FruitUser[hrDataParset.getList().size()]));
        return hrDataParset.getList();
    }

    @Override
    public List<FruitUser> findByUser(FruitUser user) {

        return null;
    }

    @Override
    public FruitUser findByUserId(String userId) {
        return null;
    }
}
