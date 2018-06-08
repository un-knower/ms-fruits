package wowjoy.fruits.ms.dao.notepad;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.logs.service.ServiceLogs;
import wowjoy.fruits.ms.dao.relation.impl.NotepadResourceDaoImpl;
import wowjoy.fruits.ms.dao.resource.ServiceResource;
import wowjoy.fruits.ms.dao.team.AbstractDaoTeam;
import wowjoy.fruits.ms.dao.user.UserDaoImpl;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.notepad.FruitNotepad;
import wowjoy.fruits.ms.module.notepad.FruitNotepadDao;
import wowjoy.fruits.ms.module.notepad.FruitNotepadExample;
import wowjoy.fruits.ms.module.notepad.mapper.FruitNotepadMapper;
import wowjoy.fruits.ms.module.relation.entity.NotepadResourceRelation;
import wowjoy.fruits.ms.module.relation.example.NotepadResourceRelationExample;
import wowjoy.fruits.ms.module.relation.mapper.NotepadResourceRelationMapper;
import wowjoy.fruits.ms.module.team.FruitTeamDao;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.*;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toCollection;

/**
 * Created by wangziwen on 2017/8/25.
 */
@Service
@Transactional
public class DaoNotepad extends ServiceNotepad {
    private final FruitNotepadMapper mapper;
    private final ServiceLogs daoLogs;
    private final UserDaoImpl userDaoImpl;
    private final AbstractDaoTeam daoTeamImpl;
    private final ServiceResource serviceResource;
    private final NotepadResourceDaoImpl<NotepadResourceRelation, NotepadResourceRelationExample> notepadResourceDao;
    private final NotepadResourceRelationMapper notepadResourceRelationMapper;

    @Autowired
    public DaoNotepad(FruitNotepadMapper mapper, ServiceLogs daoLogs, UserDaoImpl userDaoImpl, AbstractDaoTeam daoTeamImpl, ServiceResource serviceResource, NotepadResourceDaoImpl<NotepadResourceRelation, NotepadResourceRelationExample> notepadResourceDao, NotepadResourceRelationMapper notepadResourceRelationMapper) {
        this.mapper = mapper;
        this.daoLogs = daoLogs;
        this.userDaoImpl = userDaoImpl;
        this.daoTeamImpl = daoTeamImpl;
        this.serviceResource = serviceResource;
        this.notepadResourceDao = notepadResourceDao;
        this.notepadResourceRelationMapper = notepadResourceRelationMapper;
    }

    public List<FruitNotepadDao> finds(Consumer<FruitNotepadExample> exampleConsumer) {
        FruitNotepadExample example = new FruitNotepadExample();
        exampleConsumer.accept(example);
        return mapper.selectByExample(example);
    }

    @Override
    public void insert(FruitNotepad.Insert insert) {
        try {
            this.mapper.insertSelective(insert);
        } catch (DuplicateKeyException duplicate) {
            throw new CheckException(FruitDict.Exception.Check.NOTEPAD_DUPLICATE.name());
        }
        this.addResource(insert.getUploads(), insert.getUuid());
    }

    /*添加关联资源*/
    private void addResource(ArrayList<FruitNotepad.Upload> uploads, String notepad) {
        Optional.ofNullable(uploads)
                .ifPresent(resources -> resources.stream()
                        .peek(upload -> Optional.ofNullable(upload)
                                .filter(resource -> resource.getOutputStream() == null)
                                .ifPresent(FruitNotepad.Upload::base64ToOutputStream))
                        .peek(serviceResource::upload)
                        .forEach(upload -> notepadResourceDao.insert(relation -> {
                            relation.setNrType(upload.getNrType());
                            relation.setNotepadId(notepad);
                            relation.setResourceId(upload.getUuid());
                        })));
    }

    @Override
    protected void update(Consumer<FruitNotepad.Update> updateConsumer, Consumer<FruitNotepadExample> notepadExampleConsumer) {
        FruitNotepad.Update update = new FruitNotepad.Update();
        FruitNotepadExample example = new FruitNotepadExample();
        updateConsumer.accept(update);
        notepadExampleConsumer.accept(example);
        Optional.ofNullable(example.getOredCriteria().stream().filter(criteria -> !criteria.getAllCriteria().isEmpty()).collect(toCollection(ArrayList::new)))
                .filter(predicate -> !predicate.isEmpty())
                .orElseThrow(() -> new CheckException("search term can't null"));
        Optional.ofNullable(update.getUuid())
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name()));
        /*删除关联资源*/
        Optional.ofNullable(update.getRemoveResources())
                .filter(ids -> !ids.isEmpty())
                .ifPresent(ids -> notepadResourceDao.deleted(notepadResourceRelationExample -> notepadResourceRelationExample.createCriteria().andResourceIdIn(ids).andNotepadIdEqualTo(update.getUuid()).andIsDeletedEqualTo(FruitDict.Systems.N.name())));
        this.addResource(update.getUploads(), update.getUuid());
        mapper.updateByExampleSelective(update, example);
    }

    @Override
    protected void delete(FruitNotepadDao dao) {
        checkUuid(dao.getUuid());
        FruitNotepadExample example = new FruitNotepadExample();
        FruitNotepadExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckException("缺少删除条件");
        mapper.deleteByExample(example);
    }

    @Override
    protected List<FruitNotepadDao> findsByExampleAndCustom(Consumer<FruitNotepadExample> exampleConsumer, Consumer<List<String>> customConsumer) {
        FruitNotepadExample notepadExample = new FruitNotepadExample();
        List<String> customs = Lists.newLinkedList();
        customConsumer.accept(customs);
        exampleConsumer.accept(notepadExample);
        return mapper.selectByCustom(notepadExample, customs);
    }

    @Override
    protected Map<String, ArrayList<FruitLogs.Info>> joinLogs(LinkedList<String> ids) {
        if (ids == null || ids.isEmpty()) return Maps.newHashMap();
        return daoLogs.findLogs(example -> {
            example.createCriteria().andFruitTypeEqualTo(FruitDict.Parents.NOTEPAD.name()).andFruitUuidIn(ids);
            example.setOrderByClause("flogs.create_date_time desc");
        }, FruitDict.Parents.NOTEPAD);
    }

    @Override
    protected List<FruitUserDao> joinUser(LinkedList<String> ids) {
        if (ids == null || ids.isEmpty()) return Lists.newArrayList();
        return userDaoImpl.findExample(example -> example.createCriteria().andUserIdIn(ids).andIsDeletedEqualTo(FruitDict.Systems.N.name()));
    }

    @Override
    public FruitTeamDao findTeamInfo(String teamId, Consumer<FruitUserExample> userExampleConsumer) {
        return daoTeamImpl.findInfo(teamId, userExampleConsumer);
    }

    private void checkUuid(String uuid) {
        Optional.ofNullable(uuid).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name()));
        FruitNotepadExample example = new FruitNotepadExample();
        FruitNotepadExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(uuid))
            criteria.andUuidEqualTo(uuid);
        List<FruitNotepadDao> datas = mapper.selectByExample(example);
        if (datas.isEmpty())
            throw new CheckException(FruitDict.Exception.Check.SYSTEM_NOT_EXISTS.name());
    }

    @Override
    protected ArrayList<String> findResourceId(FruitDict.Resource type, String notepadId) {
        return notepadResourceRelationMapper.selectByNotepadId(type, notepadId);
    }

}
