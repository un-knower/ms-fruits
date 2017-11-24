package wowjoy.fruits.ms.aspectj;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wowjoy.fruits.ms.dao.logs.AbstractDaoLogs;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.logs.FruitLogsVo;
import wowjoy.fruits.ms.util.ApplicationContextUtils;
import wowjoy.fruits.ms.util.AsmClassInfo;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangziwen on 2017/11/16.
 * 业务级日志记录AOP
 */
@Aspect
@Component
public class LogsAspectj {

    @Autowired
    private AbstractDaoLogs logsDao;

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
        Map<String, Object> methodParamValue = methodParamValue(joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), joinPoint.getArgs());
        String uuid = replaceFormat(logInfo.uuid(), methodParamValue);
        if (uuid.equals(logInfo.uuid()))
            uuid = "日志记录异常，检查当前方法使用方式是否正确";
        String logMsg = replaceObject(ApplicationContextUtils.getCurrentUser(), replaceFormat(logInfo.format(), methodParamValue), "user");
        FruitLogsVo vo = FruitLogs.getVo();
        vo.setFruitUuid(uuid);
        vo.setFruitType(logInfo.type());
        vo.setOperateType(logInfo.operateType());
        vo.setContent(logMsg);
        vo.setUserId(ApplicationContextUtils.getCurrentUser().getUserId());
        logsDao.insert(vo);
    }

    private String replaceFormat(String format, Map<String, Object> methodParamValue) {
        String result = format;
        for (Map.Entry<String, Object> arg : methodParamValue.entrySet()) {
                /*判断是不是对象*/
            if (new Gson().toJsonTree(arg.getValue()).isJsonObject())
                result = replaceObject(arg.getValue(), format, arg.getKey());
            else
                result = result.replace(arg.getKey(), toString(arg.getValue()));
        }
        return result;
    }

    private String replaceObject(Object obj, String format, String prefix) {
        String result = format;
        for (Map.Entry<String, Object> field : fieldValue(obj).entrySet())
            result = result.replace(prefix + "." + field.getKey(), toString(field.getValue()));
        return result;
    }

    private Map<String, Object> fieldValue(Object obj) {
        Map<String, Object> fieldValue = Maps.newLinkedHashMap();
        for (Field field : findFields(obj.getClass())) {
            try {
                field.setAccessible(true);
                fieldValue.put(field.getName(), field.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new CheckException("日志记录获取参数错误");
            }
        }
        return fieldValue;
    }

    /*参数名-数据*/
    private Map<String, Object> methodParamValue(String className, String methodName, Object[] args) {
        HashMap<String, Object> result = Maps.newHashMap();
        List<String> parameterName = AsmClassInfo.newInstance(className).findParameterName(methodName);
        for (int i = 0; i < parameterName.size(); i++) {
            result.put(parameterName.get(i), args[i]);
        }
        return result;
    }

    private List<Field> findFields(Class<?> aclass) {
        List<Field> fields = Lists.newLinkedList();
        fields.addAll(Arrays.asList(aclass.getDeclaredFields()));
        if (!aclass.getSuperclass().getName().equals(Object.class.getName()))
            fields.addAll(findFields(aclass.getSuperclass()));
        return fields;
    }

    private String toString(Object obj) {
        JsonElement arg = new Gson().toJsonTree(obj);
        String result = null;
        if (arg.isJsonArray() || arg.isJsonObject())
            result = arg.toString();
        else if (arg.isJsonPrimitive())
            result = arg.getAsString();
        else if (arg.isJsonNull())
            result = "";
        return result;
    }

//    private String parserFormat(final String format) {
//        String data = format;
//        data.substring(data.indexOf("{"), data.length());
//    }

}
