package wowjoy.fruits.ms.exception;

/**
 * Created by wangziwen on 2017/11/7.
 */
public class ExceptionSupport extends RuntimeException {
    public ExceptionSupport() {
    }

    public ExceptionSupport(String message) {
        super(message);
    }

    public ExceptionSupport(String message, Throwable cause) {
        super(message, cause);
    }

    public ExceptionSupport(Throwable cause) {
        super(cause);
    }

    public ExceptionSupport(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
