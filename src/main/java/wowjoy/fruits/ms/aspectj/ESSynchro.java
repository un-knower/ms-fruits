package wowjoy.fruits.ms.aspectj;

import java.lang.annotation.*;

/**
 * Created by wangziwen on 2017/9/22.
 * 同步更新es索引库
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ESSynchro {
    Class<? extends InterfaceEsRunnable> tClass();
}
