package wowjoy.fruits.ms.dao.versions;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.project.ProjectDaoImpl;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.defect.mapper.FruitDefectMapper;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.user.mapper.FruitUserMapper;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Exception.Check;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;
import wowjoy.fruits.ms.module.versions.FruitVersions;
import wowjoy.fruits.ms.module.versions.FruitVersionsExample;
import wowjoy.fruits.ms.module.versions.mapper.FruitVersionsMapper;

import java.util.*;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toMap;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
@Service
@Transactional
public class DaoVersions extends ServiceVersions {

    private FruitVersionsMapper versionsMapper;
    private FruitUserMapper userMapper;
    private ProjectDaoImpl projectDao;
    private final FruitDefectMapper defectMapper;

    @Autowired
    public DaoVersions(FruitVersionsMapper versionsMapper, FruitUserMapper userMapper, ProjectDaoImpl project, FruitDefectMapper defectMapper) {
        this.versionsMapper = versionsMapper;
        this.userMapper = userMapper;
        this.projectDao = project;
        this.defectMapper = defectMapper;
    }

    @Override
    public void insert(FruitVersions.Insert insert) {
        versionsMapper.insertSelective(insert);
    }

    @Override
    public List<FruitVersions> findByExample(Consumer<FruitVersionsExample> exampleConsumer) {
        FruitVersionsExample example = new FruitVersionsExample();
        exampleConsumer.accept(example);
        return versionsMapper.selectByExample(example);
    }

    @Override
    protected Page<FruitVersions> findByVersionAndPage(String projectId, String version, int pageNum, int pageSize) {
        PageHelper.startPage(Optional.of(pageNum).filter(num -> num > 0).orElse(1), Optional.of(pageSize).filter(size -> size > 0).orElse(10));
        return (Page<FruitVersions>) versionsMapper.selectByProjectId(projectId, Optional.ofNullable(version).orElse("") + "%");
    }

    @Override
    protected ArrayList<FruitVersions> findByProjectAndParentIds(String projectId, LinkedList<String> parentIds, String version) {
        return versionsMapper.selectByProjectAndParentIds(projectId, parentIds, Optional.ofNullable(version).orElse("") + "%");
    }

    @Override
    public void update(FruitVersions.Update update, Consumer<FruitVersionsExample> exampleConsumer) {
        FruitVersionsExample example = new FruitVersionsExample();
        exampleConsumer.accept(example);
        Optional.ofNullable(update)
                .map(FruitVersions::getUuid)
                .filter(StringUtils::isNotBlank)
                .ifPresent(versions -> versionsMapper.updateByExampleSelective(update, example));
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

    @Override
    protected Map<String, String> findJoinDefect(ArrayList<String> versionIds) {
        return versionsMapper.selectJoinDefect(versionIds).stream().collect(toMap(str -> str, str -> str));
    }

    @Override
    public void remove(String versionId) {
        Optional.ofNullable(versionId)
                .filter(StringUtils::isNotBlank)
                .ifPresent(id -> {
                    Optional.of(versionsMapper.selectSonCount(id))
                            .filter(count -> count == 0)
                            .orElseThrow(() -> new CheckException(Check.VERSION_DELETE_INCLUDE.name()));
                    Optional.of(versionsMapper.selectJoinDefectCount(versionId))
                            .filter(count -> count == 0)
                            .orElseThrow(() -> new CheckException(Check.VERSION_DELETE_USE.name()));
                    FruitVersions version = new FruitVersions.Update();
                    version.setIsDeleted(Systems.Y.name());
                    FruitVersionsExample example = new FruitVersionsExample();
                    example.createCriteria().andUuidEqualTo(versionId);
                    versionsMapper.updateByExampleSelective(version, example);
                });
    }

}
