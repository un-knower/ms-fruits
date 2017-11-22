package wowjoy.fruits.ms.aspectj;

import java.lang.annotation.*;

/**
 * Created by wangziwen on 2017/11/21.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogInfo {
    String format() default "";
}
