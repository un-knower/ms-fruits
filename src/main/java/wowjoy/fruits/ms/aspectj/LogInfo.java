package wowjoy.fruits.ms.aspectj;

import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.lang.annotation.*;

/**
 * Created by wangziwen on 2017/11/21.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogInfo {
    /*日志模板，数据使用参数列表参数名加字段名，例如：vo.uuid*/
    String format() default "";

    /*查找到当前日志的uuid，使用方式和format相同*/
    String uuid();

    /*日志类型*/
    FruitDict.Parents type();

    /*日志操作类型*/
    FruitDict.LogsDict operateType();
}
