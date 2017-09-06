package wowjoy.fruits.ms.dao.project;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.project.example.FruitProjectExample;
import wowjoy.fruits.ms.module.project.mapper.FruitProjectMapper;

import java.util.List;

/**
 * Created by wangziwen on 2017/9/6.
 */
@Service
@Transactional
public class ProjectDaoImpl extends AbstractDaoProject {

    @Autowired
    private FruitProjectMapper projectMapper;

    @Override
    protected void insert() {
        projectMapper.insertSelective(this.getFruitProject());
    }

    @Override
    protected List<FruitProject> findByProject() {
        FruitProjectExample example = new FruitProjectExample();
        final FruitProjectExample.Criteria criteria = example.createCriteria();
        if (this.getFruitProject().isNotEmpty()) {
            if (StringUtils.isNotBlank(this.getFruitProject().getTitle()))
                criteria.andTitleEqualTo(this.getFruitProject().getTitle());
        }
        return projectMapper.selectByExample(example);
    }
}
