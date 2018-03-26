package wowjoy.fruits.ms.dao.notepad;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.dao.logs.service.ServiceLogs;
import wowjoy.fruits.ms.dao.team.AbstractDaoTeam;
import wowjoy.fruits.ms.dao.user.UserDaoImpl;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.notepad.FruitNotepadDao;
import wowjoy.fruits.ms.module.notepad.FruitNotepadExample;
import wowjoy.fruits.ms.module.notepad.mapper.FruitNotepadMapper;
import wowjoy.fruits.ms.module.team.FruitTeamDao;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by wangziwen on 2017/8/25.
 */
@Service
@Transactional
public class NotepadDaoImpl extends AbstractDaoNotepad {
    private final FruitNotepadMapper mapper;
    private final ServiceLogs daoLogs;
    private final UserDaoImpl userDaoImpl;
    private final AbstractDaoTeam daoTeamImpl;

    @Autowired
    public NotepadDaoImpl(FruitNotepadMapper mapper, ServiceLogs daoLogs, UserDaoImpl userDaoImpl, AbstractDaoTeam daoTeamImpl) {
        this.mapper = mapper;
        this.daoLogs = daoLogs;
        this.userDaoImpl = userDaoImpl;
        this.daoTeamImpl = daoTeamImpl;
    }

    public List<FruitNotepadDao> finds(Consumer<FruitNotepadExample> exampleConsumer) {
        FruitNotepadExample example = new FruitNotepadExample();
        exampleConsumer.accept(example);
        return mapper.selectByExample(example);
    }

    @Override
    public void insert(FruitNotepadDao dao) {
        mapper.insertSelective(dao);
    }

    @Override
    protected void update(FruitNotepadDao dao) {
        checkUuid(dao.getUuid());
        FruitNotepadExample example = new FruitNotepadExample();
        FruitNotepadExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        if (criteria.getAllCriteria().isEmpty())
            throw new CheckException("缺少更新条件");
        mapper.updateByExampleSelective(dao, example);
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
    protected Map<String, LinkedList<FruitLogs.Info>> joinLogs(LinkedList<String> ids) {
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
        if (StringUtils.isBlank(uuid))
            throw new CheckException("uuid");
        FruitNotepadExample example = new FruitNotepadExample();
        FruitNotepadExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(uuid))
            criteria.andUuidEqualTo(uuid);
        List<FruitNotepadDao> datas = mapper.selectByExample(example);
        if (datas.isEmpty())
            throw new CheckException("日志不存在");
    }

}
