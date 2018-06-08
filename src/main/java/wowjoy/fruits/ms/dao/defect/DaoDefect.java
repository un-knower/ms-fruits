package wowjoy.fruits.ms.dao.defect;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.comment.ServiceComment;
import wowjoy.fruits.ms.dao.logs.service.ServiceLogs;
import wowjoy.fruits.ms.dao.project.ProjectDaoImpl;
import wowjoy.fruits.ms.dao.relation.RelationInterface;
import wowjoy.fruits.ms.dao.relation.impl.DefectCommentDaoImpl;
import wowjoy.fruits.ms.dao.relation.impl.DefectResourceDaoImpl;
import wowjoy.fruits.ms.dao.resource.ServiceResource;
import wowjoy.fruits.ms.dao.user.UserDaoImpl;
import wowjoy.fruits.ms.dao.versions.DaoVersions;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.comment.DefectComment;
import wowjoy.fruits.ms.module.comment.FruitComment;
import wowjoy.fruits.ms.module.defect.*;
import wowjoy.fruits.ms.module.defect.mapper.DefectStatusCountMapper;
import wowjoy.fruits.ms.module.defect.mapper.FruitDefectMapper;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.relation.entity.DefectCommentRelation;
import wowjoy.fruits.ms.module.relation.entity.DefectResourceRelation;
import wowjoy.fruits.ms.module.relation.example.DefectCommentRelationExample;
import wowjoy.fruits.ms.module.relation.example.DefectResourceRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.DefectResourceRelationMapper;
import wowjoy.fruits.ms.module.resource.FruitResource;
import wowjoy.fruits.ms.module.resource.mapper.FruitResourceMapper;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.versions.FruitVersions;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toCollection;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
@Service
@Transactional
public class DaoDefect extends ServiceDefect {

    private final FruitDefectMapper defectMapper;
    private final RelationInterface<DefectResourceRelation, DefectResourceRelationExample> defectResourceRelation;
    private final DefectStatusCountMapper countMapper;
    private final ServiceResource serviceResource;
    private final ProjectDaoImpl projectDao;
    private final UserDaoImpl userDao;
    private final DaoVersions daoVersions;
    private final FruitResourceMapper resourceMapper;
    private final ServiceLogs serviceLogs;
    private final DefectCommentDaoImpl<DefectCommentRelation, DefectCommentRelationExample> defectCommentDao;
    private final ServiceComment serviceComment;
    private final DefectResourceRelationMapper defectResourceRelationMapper;

    @Autowired
    public DaoDefect(FruitDefectMapper defectMapper, DefectResourceDaoImpl<DefectResourceRelation, DefectResourceRelationExample> defectResourceRelation, DefectStatusCountMapper countMapper, ServiceResource serviceResource, ProjectDaoImpl projectDao, UserDaoImpl userDao, DaoVersions daoVersions, FruitResourceMapper resourceMapper, ServiceLogs serviceLogs, DefectCommentDaoImpl<DefectCommentRelation, DefectCommentRelationExample> defectCommentDao, ServiceComment serviceComment, DefectResourceRelationMapper defectResourceRelationMapper) {
        this.defectMapper = defectMapper;
        this.defectResourceRelation = defectResourceRelation;
        this.countMapper = countMapper;
        this.serviceResource = serviceResource;
        this.projectDao = projectDao;
        this.userDao = userDao;
        this.daoVersions = daoVersions;
        this.resourceMapper = resourceMapper;
        this.serviceLogs = serviceLogs;
        this.defectCommentDao = defectCommentDao;
        this.serviceComment = serviceComment;
        this.defectResourceRelationMapper = defectResourceRelationMapper;
    }

    @Override
    public void insert(FruitDefect.Insert insert) {
        /*添加资源关联信息*/
        this.addResource(insert.getUpload(), insert.getUuid());
        defectMapper.insertSelective(insert);
    }

    /*添加关联资源*/
    private void addResource(ArrayList<FruitDefect.Upload> uploads, String defectId) {
        Optional.ofNullable(uploads)
                .ifPresent(resources -> resources.stream()
                        .peek(serviceResource::upload)
                        .forEach(upload -> defectResourceRelation.insert(relation -> {
                            relation.setDrType(upload.getDrType());
                            relation.setDefectId(defectId);
                            relation.setResourceId(upload.getUuid());
                        })));
    }

    @Override
    public ArrayList<FruitDefect> findConsumer(Consumer<FruitDefectExample> exampleConsumer) {
        FruitDefectExample example = new FruitDefectExample();
        exampleConsumer.accept(example);
        return defectMapper.selectByExample(example);
    }

    @Override
    public Optional<FruitDefect> findWithBlobs(String uuid) {
        FruitDefectExample example = new FruitDefectExample();
        example.createCriteria().andUuidEqualTo(uuid);
        return defectMapper.selectByExampleWithBLOBs(example).stream().findAny();
    }

    @Override
    public Page<FruitDefect> findConsumerPage(Consumer<FruitDefect.Search> exampleConsumer) {
        FruitDefect.Search search = new FruitDefect.Search();
        exampleConsumer.accept(search);
        PageHelper.startPage(Optional.of(search.getPageNum()).filter(i -> i > 0).orElse(1), Optional.of(search.getPageSize()).filter(i -> i > 0).orElse(10));
        return (Page<FruitDefect>) defectMapper.selectByExampleExt(search);
    }

    @Override
    public void update(Consumer<FruitDefect.Update> updateConsumer, Consumer<FruitDefectExample> exampleConsumer) {
        FruitDefect.Update update = new FruitDefect.Update();
        FruitDefectExample example = new FruitDefectExample();
        updateConsumer.accept(update);
        exampleConsumer.accept(example);
        Optional.ofNullable(update.getRemoveResources())
                .filter(deletes -> !deletes.isEmpty())
                .ifPresent(ids -> defectResourceRelation.deleted(defectResourceRelationExample -> defectResourceRelationExample.createCriteria().andResourceIdIn(ids).andDefectIdEqualTo(update.getUuid()).andIsDeletedEqualTo(FruitDict.Systems.N.name())));
        this.addResource(update.getUpload(), update.getUuid());
        defectMapper.updateByExampleSelective(update, example);
    }

    @Override
    public ArrayList<FruitDefectResource> findResourceByDefectId(ArrayList<String> defectIds) {
        return resourceMapper.selectByDefectId(defectIds);
    }

    @Override
    public List<FruitProject> findProjectByProjectId(ArrayList<String> projectIds) {
        return projectDao.finds(example -> example.createCriteria().andUuidIn(projectIds));
    }

    @Override
    public List<FruitUserDao> findUserByUserIds(ArrayList<String> userIds) {
        return userDao.findExample(example -> example.createCriteria().andUserIdIn(userIds));
    }

    @Override
    public Optional<FruitUserDao> findUserByUserId(Consumer<FruitUserExample> exampleConsumer) {
        return Optional.ofNullable(userDao.findExample(exampleConsumer)).flatMap(users -> users.stream().findAny());
    }

    @Override
    public List<FruitVersions> findVersionByVersionId(ArrayList<String> versionIds) {
        return daoVersions.findByExample(versionsExample -> versionsExample.createCriteria().andUuidIn(versionIds));
    }

    @Override
    public void defectCount(Consumer<DefectStatusCount> consumer) {
        DefectStatusCount count = new DefectStatusCount();
        consumer.accept(count);
        countMapper.insertOnDuplicatedUpdate(count);
    }

    @Override
    public Map<String, ArrayList<FruitLogs.Info>> findLogsByDefectId(String defectId) {
        return serviceLogs.findLogs(example -> {
            example.createCriteria().andFruitUuidEqualTo(defectId).andFruitTypeEqualTo(FruitDict.Parents.DEFECT.name());
            example.setOrderByClause("flogs.create_date_time desc");
        }, FruitDict.Parents.DEFECT);
    }

    @Override
    public int findStatusRepeatCount(String defectId, FruitDict.DefectDict.Status status) {
        DefectStatusCountExample example = new DefectStatusCountExample();
        example.createCriteria().andDefectIdEqualTo(defectId).andDefectStatusEqualTo(status.name()).andIsDeletedEqualTo(FruitDict.Systems.N.name());
        return Optional.ofNullable(defectId)
                .filter(StringUtils::isNotBlank)
                .map(id -> countMapper.selectByExample(example))
                .flatMap(counts -> counts.stream().findAny())
                .map(DefectStatusCount::getCount)
                .orElse(0);

    }

    @Override
    public void insertComment(FruitComment.Insert insert, String defectId) {
        Optional.ofNullable(defectId)
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name()));
        Optional.ofNullable(insert.getComment())
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.COMMENT_CONTENT_NULL.name()));
        insert.setUserId(ApplicationContextUtils.getCurrentUser().getUserId());
        serviceComment.insert(insert);
        defectCommentDao.insert(defectCommentRelation -> {
            defectCommentRelation.setCommentId(insert.getUuid());
            defectCommentRelation.setDefectId(defectId);
        });
    }

    @Override
    public ArrayList<DefectComment> findComment(String defectId) {
        return serviceComment.commentTree(serviceComment.joinUser(() -> serviceComment.findDefect(defectId)));
    }

    @Override
    public DefectDuplicate findDuplicate(String defectId) {
        return defectMapper.selectDuplicate(defectId);
    }

    @Override
    public ArrayList<String> findResourceId(FruitDict.Resource type, String defectId) {
        return defectResourceRelationMapper.selectByDefectId(type, defectId).stream().map(DefectResourceRelation::getResourceId).collect(toCollection(ArrayList::new));
    }
}
