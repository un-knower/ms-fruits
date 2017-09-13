package wowjoy.fruits.ms.dao;

import java.text.MessageFormat;

/**
 * Created by wangziwen on 2017/9/12.
 */
public class DaoException extends RuntimeException {
    public DaoException(String message) {
        super(MessageFormat.format("dao error ：{0}", message));
    }
}
