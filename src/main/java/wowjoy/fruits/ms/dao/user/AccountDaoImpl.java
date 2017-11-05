package wowjoy.fruits.ms.dao.user;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.user.FruitAccount;
import wowjoy.fruits.ms.module.user.FruitAccountDao;
import wowjoy.fruits.ms.module.user.example.FruitAccountExample;
import wowjoy.fruits.ms.module.user.mapper.FruitAccountMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wangziwen on 2017/10/26.
 */
@Service
@Transactional
public class AccountDaoImpl extends AbstractDaoAccount {
    @Autowired
    private FruitAccountMapper accountMapper;

    @Override
    protected void inserts(List<FruitAccountDao> accountDaos) {
        if (accountDaos.isEmpty())
            throw new CheckException("无可批量添加账户");
        accountMapper.inserts(accountDaos);
    }

    @Override
    protected List<FruitAccountDao> finds(FruitAccountDao dao) {
        FruitAccountExample example = new FruitAccountExample();
        FruitAccountExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getPrincipal()))
            criteria.andPrincipalEqualTo(dao.getPrincipal());
        return accountMapper.relationUser(example);
    }

    @Override
    protected void clearByUserId(String... ids) {
        FruitAccountExample example = new FruitAccountExample();
        example.createCriteria().andUserIdIn(Arrays.asList(ids));
        accountMapper.deleteByExample(example);
        FruitAccountDao dao = FruitAccount.getDao();
        dao.setIsDeleted(FruitDict.Dict.Y.name());
        accountMapper.updateByExampleSelective(dao, new FruitAccountExample());
    }
}
