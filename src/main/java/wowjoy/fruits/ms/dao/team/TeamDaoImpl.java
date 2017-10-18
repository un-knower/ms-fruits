package wowjoy.fruits.ms.dao.team;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.module.team.FruitTeamDao;
import wowjoy.fruits.ms.module.team.FruitTeamExample;
import wowjoy.fruits.ms.module.team.mapper.FruitTeamMapper;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by wangziwen on 2017/9/5.
 */
// TODO: 2017/10/13 团队关联项目时，需要指明在团队在项目中的角色
@Service
@Transactional
public class TeamDaoImpl extends AbstractDaoTeam {
    @Autowired
    private FruitTeamMapper mapper;

    @Override
    protected List<FruitTeamDao> finds(FruitTeamDao dao) {
        final FruitTeamExample example = new FruitTeamExample();
        final FruitTeamExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        if (StringUtils.isNotBlank(dao.getTitle()))
            criteria.andTitleLike(MessageFormat.format("%{0}%", dao.getTitle()));
        return mapper.selectByExample(example);
    }

    @Override
    protected List<FruitTeamDao> findRelation(FruitTeamDao dao) {
        final FruitTeamExample example = new FruitTeamExample();
        final FruitTeamExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        if (StringUtils.isNotBlank(dao.getTitle()))
            criteria.andTitleLike(MessageFormat.format("%{0}%", dao.getTitle()));
        return mapper.selectUserRelationByExample(example);
    }
}
