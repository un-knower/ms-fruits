package wowjoy.fruits.ms.aspectj;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.util.ApplicationContextUtils;
import wowjoy.fruits.ms.util.AsmClassInfo;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangziwen on 2017/11/16.
 * 业务级日志记录AOP
 */
@Aspect
@Component
public class LogsAspectj {
    @Pointcut("@annotation(wowjoy.fruits.ms.aspectj.LogInfo)")
    public void myAnnotation() {
    }

    @Around("myAnnotation() && @annotation(annotation)")
    public Object around(ProceedingJoinPoint joinPoint, LogInfo annotation) {
        try {
            Object result = joinPoint.proceed();
            record(joinPoint, annotation);
            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    public void record(ProceedingJoinPoint joinPoint, LogInfo logInfo) {
        String logMsg = this.replace(joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), logInfo.format(), joinPoint.getArgs());
        logMsg = this.replace(ApplicationContextUtils.getCurrentUser(), "user", logMsg);
        LoggerFactory.getLogger(this.getClass()).info(logMsg);
    }

    private String replace(String className, String methodName, String format, Object... args) {
        String result = format;
        List<String> parameterName = AsmClassInfo.newInstance(className).findParameterName(methodName);
        for (int i = 0; i < parameterName.size(); i++) {
            result = replace(args[i], parameterName.get(i), format);
        }
        return result;
    }

    private String getTarget(String target) {
        return "{" + target + "}";
    }

    private List<Field> findFields(Class<?> aclass) {
        List<Field> fields = Lists.newLinkedList();
        fields.addAll(Arrays.asList(aclass.getDeclaredFields()));
        if (!aclass.getSuperclass().getName().equals(Object.class.getName()))
            fields.addAll(findFields(aclass.getSuperclass()));
        return fields;
    }

    private String replace(Object obj, String prefix, String format) {
        String result = format;
        for (Field field : findFields(obj.getClass())) {
            try {
                field.setAccessible(true);
                result = result.replace(getTarget(prefix + "." + field.getName()), new Gson().toJsonTree(field.get(obj)).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new CheckException("日志记录获取参数错误");
            }
        }
        return result;
    }


}
