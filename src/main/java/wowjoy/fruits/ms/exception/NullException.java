package wowjoy.fruits.ms.exception;

/**
 * Created by wangziwen on 2017/9/12.
 */
public class NullException extends RuntimeException {
    public NullException(String message) {
        super("【Null exception】" + message);
    }
}
