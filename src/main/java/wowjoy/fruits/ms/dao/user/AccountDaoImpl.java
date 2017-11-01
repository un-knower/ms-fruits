package wowjoy.fruits.ms.dao.user;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.user.FruitAccountDao;
import wowjoy.fruits.ms.module.user.example.FruitAccountExample;
import wowjoy.fruits.ms.module.user.mapper.FruitAccountMapper;

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
            throw new CheckException("无可批量添加账单");
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
}
