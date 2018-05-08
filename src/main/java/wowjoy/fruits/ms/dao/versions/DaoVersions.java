package wowjoy.fruits.ms.dao.versions;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.project.ProjectDaoImpl;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.user.mapper.FruitUserMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;
import wowjoy.fruits.ms.module.versions.FruitVersions;
import wowjoy.fruits.ms.module.versions.FruitVersionsExample;
import wowjoy.fruits.ms.module.versions.mapper.FruitVersionsMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
@Service
@Transactional
public class DaoVersions extends ServiceVersions {

    private FruitVersionsMapper versionsMapper;
    private FruitUserMapper userMapper;
    private ProjectDaoImpl projectDao;

    @Autowired
    public DaoVersions(FruitVersionsMapper versionsMapper, FruitUserMapper userMapper, ProjectDaoImpl project) {
        this.versionsMapper = versionsMapper;
        this.userMapper = userMapper;
        this.projectDao = project;
    }

    @Override
    protected void insert(FruitVersions.Insert insert) {
        versionsMapper.insertSelective(insert);
    }

    @Override
    public List<FruitVersions> findByExample(Consumer<FruitVersionsExample> exampleConsumer) {
        FruitVersionsExample example = new FruitVersionsExample();
        exampleConsumer.accept(example);
        return versionsMapper.selectByExample(example);
    }

    @Override
    protected Page<FruitVersions> findByExampleAndPage(Consumer<FruitVersionsExample> exampleConsumer, int pageNum, int pageSize) {
        FruitVersionsExample example = new FruitVersionsExample();
        exampleConsumer.accept(example);
        PageHelper.startPage(Optional.of(pageNum).filter(num -> num > 0).orElse(1), Optional.of(pageSize).filter(size -> size > 0).orElse(10));
        return (Page<FruitVersions>) versionsMapper.selectByExample(example);
    }

    @Override
    protected void update(FruitVersions.Update update, Consumer<FruitVersionsExample> exampleConsumer) {
        FruitVersionsExample example = new FruitVersionsExample();
        exampleConsumer.accept(example);
        Optional.ofNullable(update)
                .map(FruitVersions::getUuid)
                .filter(StringUtils::isNotBlank)
                .ifPresent(versions -> versionsMapper.updateByExampleSelective(update, example));
    }

    @Override
    protected ArrayList<FruitVersions> findByExampleOrUserExample(Consumer<FruitVersionsExample> versionsExampleConsumer, Consumer<FruitUserExample> userExampleConsumer) {
        FruitVersionsExample versionsExample = new FruitVersionsExample();
        FruitUserExample userExample = new FruitUserExample();
        versionsExampleConsumer.accept(versionsExample);
        userExampleConsumer.accept(userExample);
        return versionsMapper.selectByExampleOrUserExample(versionsExample, userExample);
    }

    @Override
    protected List<FruitUserDao> joinUser(List<String> userId) {
        FruitUserExample example = new FruitUserExample();
        example.createCriteria().andUserIdIn(userId).andIsDeletedEqualTo(Systems.N.name());
        return userMapper.selectByExample(example);
    }

    @Override
    protected List<FruitProject> joinProject(List<String> userId) {
        return projectDao.finds(example -> example.createCriteria().andIsDeletedEqualTo(Systems.N.name()).andUuidIn(userId));
    }
}
