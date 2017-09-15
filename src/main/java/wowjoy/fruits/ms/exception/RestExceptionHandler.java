package wowjoy.fruits.ms.exception;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * Created by wangziwen on 2017/9/15.
 */
@RestController
public class RestExceptionHandler {

    @ExceptionHandler(CheckException.class)
    public void checkException(CheckException exception) {
        LoggerFactory.getLogger(this.getClass()).info(exception.getMessage());
    }

    @ExceptionHandler(NullException.class)
    public void nullException(NullException exception) {
        LoggerFactory.getLogger(this.getClass()).info(exception.getMessage());
    }
}
