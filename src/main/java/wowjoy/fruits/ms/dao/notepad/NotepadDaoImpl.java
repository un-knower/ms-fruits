package wowjoy.fruits.ms.dao.notepad;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.notepad.FruitNotepad;
import wowjoy.fruits.ms.module.notepad.FruitNotepadDao;
import wowjoy.fruits.ms.module.notepad.FruitNotepadExample;
import wowjoy.fruits.ms.module.notepad.mapper.FruitNotepadMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wangziwen on 2017/8/25.
 */
@Service
@Transactional
public class NotepadDaoImpl extends AbstractDaoNotepad {
    @Autowired
    private FruitNotepadMapper mapper;

    @Override
    public FruitNotepad find(FruitNotepadDao dao) {
        FruitNotepadExample example = new FruitNotepadExample();
        FruitNotepadExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        List<FruitNotepadDao> notepads = mapper.selectByExample(example);
        if (notepads.isEmpty())
            return FruitNotepad.getEmpty();
        return notepads.get(0);
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
    protected List<FruitNotepadDao> findsByCurrentIds(FruitNotepadDao dao) {
        return mapper.selectByCurrentUser(this.findsTemplate(dao));
    }

    @Override
    protected List<FruitNotepadDao> findsByTeamIds(FruitNotepadDao dao, String... teamIds) {
        return mapper.selectByTeamIds(this.findsTemplate(dao), teamIds);
    }

    @Override
    protected List<FruitNotepadDao> joinLogs(LinkedList<String> ids) {
        return mapper.selectJoinLogsByNotepadIds(ids.toArray(new String[ids.size()]));
    }

    @Override
    protected List<FruitNotepadDao> joinUser(LinkedList<String> ids) {
        return mapper.selectJoinUserByNotepadIds(ids.toArray(new String[ids.size()]));
    }

    private FruitNotepadExample findsTemplate(FruitNotepadDao dao) {
        FruitNotepadExample example = new FruitNotepadExample();
        FruitNotepadExample.Criteria criteria = example.createCriteria();
        if (dao.getStartDate() != null && dao.getEndDate() != null)
            criteria.andEstimatedSubmitDateBetween(dao.getStartDate(), dao.getEndDate());
        if (StringUtils.isNotBlank(dao.getState()))
            criteria.andStateEqualTo(dao.getState());
        if (StringUtils.isNotBlank(dao.getUuid()))
            criteria.andUuidEqualTo(dao.getUuid());
        if (StringUtils.isNotBlank(dao.getUserId()))
            criteria.andUserIdEqualTo(dao.getUserId());
        example.setOrderByClause("notepad.estimated_submit_date desc,notepad.create_date_time desc");
        return example;
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
