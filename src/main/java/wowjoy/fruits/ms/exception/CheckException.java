package wowjoy.fruits.ms.exception;

/**
 * Created by wangziwen on 2017/9/12.
 */
public class CheckException extends RuntimeException {
    public CheckException(String message) {
        super("【Check exception】" + message);
    }
}
