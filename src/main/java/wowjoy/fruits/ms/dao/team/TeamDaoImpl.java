package wowjoy.fruits.ms.dao.team;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.module.team.FruitTeam;
import wowjoy.fruits.ms.module.team.FruitTeamExample;
import wowjoy.fruits.ms.module.team.mapper.FruitTeamMapper;

import java.util.List;

/**
 * Created by wangziwen on 2017/9/5.
 */
@Service
@Transactional
public class TeamDaoImpl extends AbstractDaoTeam{
    @Autowired
    private FruitTeamMapper mapper;

    @Override
    protected void finds() {
        
    }
}
