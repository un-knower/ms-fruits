package wowjoy.fruits.ms.dao.logs;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.logs.FruitLogsDao;
import wowjoy.fruits.ms.module.logs.FruitLogsVo;

/**
 * Created by wangziwen on 2017/8/25.
 */
public abstract class AbstractDaoLogs implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/
    protected abstract void insert(FruitLogsDao dao);

        /*******************************
         * PUBLIC 函数，公共接口         *
         * 尽量保证规范，不直接调用dao接口 *
         *******************************/

    public void insert(FruitLogsVo vo) {
        FruitLogsDao dao = FruitLogs.getDao();
        dao.setUuid(vo.getUuid());
        dao.setContent(vo.getContent());
        dao.setUserId(vo.getUserId());
        dao.setFruitUuid(vo.getFruitUuid());
        dao.setFruitType(vo.getFruitType());
        dao.setOperateType(vo.getOperateType());
        dao.setJsonObject(vo.getJsonObject());
        this.insert(dao);
    }


}
