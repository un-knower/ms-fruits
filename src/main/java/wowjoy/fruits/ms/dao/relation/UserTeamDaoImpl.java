package wowjoy.fruits.ms.dao.relation;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.module.relation.entity.UserTeamRelation;
import wowjoy.fruits.ms.module.relation.mapper.UserTeamRelationMapper;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.relation.example.UserTeamRelationExample;
import wowjoy.fruits.ms.module.user.mapper.FruitUserMapper;

import java.util.List;
import java.util.Set;

/**
 * Created by wangziwen on 2017/9/5.
 */
@Service
@Transactional
public class UserTeamDaoImpl extends AbstractDaoRelation{
    @Autowired
    private UserTeamRelationMapper userTeamRelationMapper;
    @Autowired
    private FruitUserMapper userMapper;

    @Override
    protected UserTeamRelation getAbstractEntity() {
        return (UserTeamRelation) super.getAbstractEntity();
    }

    public List<FruitUser> findByEntity(){
        final UserTeamRelationExample example = new UserTeamRelationExample();
        example.createCriteria().andTeamIdEqualTo(this.getAbstractEntity().getTeamId());
        final List<UserTeamRelation> userTeamId = userTeamRelationMapper.selectByExample(example);
        final Set<Object> ids = Sets.newLinkedHashSet();
        userTeamId.forEach((i)->{
            ids.add(i.getUserId());
        });
        final FruitUserExample userExample = new FruitUserExample();
        userExample.createCriteria().andUserIdIn(Lists.newArrayList(ids.toArray(new String[ids.size()])));
        return userMapper.selectByExample(userExample);
    }
}
