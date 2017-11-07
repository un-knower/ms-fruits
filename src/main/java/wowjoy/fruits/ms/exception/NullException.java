package wowjoy.fruits.ms.exception;

/**
 * Created by wangziwen on 2017/9/12.
 */
public class NullException extends ExceptionSupport {
    public NullException(String message) {
        super("【Null exception】" + message);
    }
}
