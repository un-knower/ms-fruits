package wowjoy.fruits.ms.module.logs.transfer.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.logs.transfer.FruitTransferLogs;
import wowjoy.fruits.ms.module.logs.transfer.FruitTransferUser;

import java.util.ArrayList;

/**
 * Created by wangziwen on 2018/3/9.
 */
public interface FruitTransferLogsMapperExt {
    ArrayList<FruitTransferUser> selectUserByTransferId(@Param("transferIds") ArrayList transferIds);
}
