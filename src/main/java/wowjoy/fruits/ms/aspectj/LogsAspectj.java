package wowjoy.fruits.ms.aspectj;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import wowjoy.fruits.ms.dao.AbstractDaoChain;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.dao.logs.AbstractDaoLogs;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.logs.FruitLogsVo;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;
import wowjoy.fruits.ms.util.AsmClassInfo;
import wowjoy.fruits.ms.util.RestResult;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by wangziwen on 2017/11/16.
 * 业务级日志记录AOP
 */
@Aspect
@Component
@Order(1)
public class LogsAspectj {

    @Qualifier("LogsDaoImpl")
    @Autowired
    private AbstractDaoLogs logsDao;

    private final String prefix = "{";
    private final String suffix = "}";

    @Pointcut("@annotation(wowjoy.fruits.ms.aspectj.LogInfo)")
    public void myAnnotation() {
    }

    @Around("myAnnotation() && @annotation(annotation)")
    public Object around(ProceedingJoinPoint joinPoint, LogInfo annotation) {
        try {
            Object result = joinPoint.proceed();
            if (((RestResult) result).getSuccess())
                InterfaceDao.DaoThread.getInstance().execute(() -> {
                    record(joinPoint, annotation);
                    return true;
                });
            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    /*记录日志*/
    public void record(ProceedingJoinPoint joinPoint, LogInfo logInfo) {
        /*获取当前操作方法对应的：参数-数据列表*/
        Map<String, Object> methodParamValue = methodParamValue(joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), joinPoint.getArgs());
        /*提取占位符关键字*/
        LinkedList<Placeholder> placeholders = extract(logInfo.format(), prefix, suffix);
        placeholders.add(Placeholder.newComma(logInfo.uuid()));
        Map<String, Placeholder> placeValues = Maps.newLinkedHashMap();
        /*从当前方法【参数-数据列表】中获取对应的数据*/
        placeholders.forEach((i) -> placeValues.put(i.getKeyPrefix(), setPlaceholder(i, methodParamValue)));
        /*根据uuid查询数据库中保存数据*/
        AbstractDaoChain daoChain = AbstractDaoChain.newInstance(logInfo.type());
        AbstractEntity DBData = daoChain.find(placeValues.get(logInfo.uuid()).getValue());
        if (DBData != null) placeValues.forEach((k, v) -> {
            if (v.getPrefix() != null && v.getValue() == null)
                v.setValue(toString(fieldValue(DBData).get(v.getKey())));
        });
        FruitLogsVo vo = logTemplate(methodParamValue, DBData, logInfo);
         /*获取对应的uuid*/
        vo.setFruitUuid(placeValues.get(logInfo.uuid()).getValue());
        /*遍历【placeholders】将字符串中占位符部分替换为对应字符串数据*/
        vo.setContent(replace(logInfo.format(), placeholders));
        /*记录日志*/
        logsDao.insert(vo);
    }

    private FruitLogsVo logTemplate(Map<String, Object> methodParamValue, AbstractEntity dbData, LogInfo logInfo) {
        FruitLogsVo vo = FruitLogs.getVo();
        if (logInfo.operateType().equals(FruitDict.Systems.UPDATE))
            methodParamValue.forEach((name, obj) -> {
                if (logInfo.mainParam().equals(name))
                    vo.setJsonObject(new Gson().toJsonTree(obj).toString());
            });
        else if (logInfo.operateType().equals(FruitDict.Systems.DELETE) || logInfo.operateType().equals(FruitDict.Systems.ADD))
            vo.setJsonObject(new Gson().toJsonTree(dbData).toString());
        vo.setFruitType(logInfo.type());
        vo.setOperateType(logInfo.operateType());
        vo.setUserId(ApplicationContextUtils.getCurrentUser().getUserId());
        return vo;
    }

    /*替换字符串中占位符部分*/
    private String replace(String format, List<Placeholder> placeholders) {
        String result = format;
        for (Placeholder placeholder : placeholders) {
            result = result.replace(MessageFormat.format("{0}{1}{2}", prefix, placeholder.getKeyPrefix(), suffix), placeholder.getValue() == null ? "" : placeholder.getValue());
        }
        return result;
    }

    /*提取字符串中占位符部分，返回一组PlaceHolder列表*/
    private LinkedList<Placeholder> extract(String format, String prefix, String suffix) {
        LinkedList<Placeholder> result = Lists.newLinkedList();
        int begin = format.indexOf(prefix);
        int end = format.indexOf(suffix);
        if (begin == -1 && end == -1)
            return result;
        result.add(Placeholder.newComma(format.substring(begin + 1, end)));
        result.addAll(extract(format.substring(end + 1, format.length()), prefix, suffix));
        return result;
    }

    /*根据占位符查询对应参数数据，并设置placeHolder的value属性，*/
    private Placeholder setPlaceholder(Placeholder placeholder, Map<String, Object> methodParamValue) {
        /*绑定默认值，如果没有满足任何条件就是数据未正确绑定*/
        if (methodParamValue.containsKey(placeholder.getKeyPrefix())) {
            /*若能进来说明肯定不是对象，是基本数据类型或String*/
            placeholder.setValue(toString(methodParamValue.get(placeholder.getKey())));
        } else if (methodParamValue.containsKey(placeholder.getPrefix()) && new Gson().toJsonTree(methodParamValue.get(placeholder.getPrefix())).isJsonObject()) {
            Map<String, Object> fieldValue = fieldValue(methodParamValue.get(placeholder.getPrefix()));
            if (fieldValue.containsKey(placeholder.getKey()))
                placeholder.setValue(toString(fieldValue.get(placeholder.getKey())));
        }
        return placeholder;
    }

    @Deprecated
    public Object fieldValueByField(Map<String, Object> fieldValue, String key) {
        if (StringUtils.isBlank(key))
            throw new CheckException("key 不能为空");
        String[] keys = key.split("\\.");
        for (String i : keys)
            if (StringUtils.isBlank(i))
                throw new CheckException("字段路径错误");
        Object data = fieldValue.get(keys[0]);
        if (keys.length == 1) return data;
        JsonElement element = null;
        int i = 0;
        while (true) {
            element = new Gson().toJsonTree(data);
            if (element.isJsonObject() && data instanceof Map)
                data = ((Map) data).get(keys[i]);
            else
                return data;
            i++;
            if (i >= keys.length || data == null)
                return data;
        }
    }

    /*获取字段名称-数据*/
    private Map<String, Object> fieldValue(Object obj) {
        Map<String, Object> fieldValue = Maps.newLinkedHashMap();
        for (Field field : findFields(obj.getClass())) {
            try {
                field.setAccessible(true);
                Object data = field.get(obj);
                if (new Gson().toJsonTree(data).isJsonObject()) {
                    if (data instanceof AbstractEntity)
                        fieldValue.put(field.getName(), data == null ? null : fieldValue(data));
                } else {
                    fieldValue.put(field.getName(), data);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new CheckException("日志记录获取参数错误");
            }
        }
        return fieldValue;
    }

    /*根据指定类的指定方法获取对应的【参数名-数据】列表*/
    private Map<String, Object> methodParamValue(String className, String methodName, Object[] args) {
        HashMap<String, Object> result = Maps.newHashMap();
        List<String> parameterName = AsmClassInfo.newInstance(className).findParameterName(methodName);
        for (int i = 0; i < parameterName.size(); i++) {
            result.put(parameterName.get(i), args[i]);
        }
        result.put("user", ApplicationContextUtils.getCurrentUser());
        return result;
    }

    /*根据指定类，通过递归获取所有集成的父类字段 and 当前字段的集合,返回【总字段集合】*/
    private List<Field> findFields(Class<?> aclass) {
        List<Field> fields = Lists.newLinkedList();
        fields.addAll(Arrays.asList(aclass.getDeclaredFields()));
        if (!aclass.getSuperclass().getName().equals(Object.class.getName()))
            fields.addAll(findFields(aclass.getSuperclass()));
        return fields;
    }

    /*将所有对象全部转换为 string 类型字符串*/
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

    /*占位符对象，包含占位符对应的数据*/
    private static class Placeholder {
        /*占位符前缀，若没有可用null代替*/
        private final String prefix;
        /*占位符key*/
        private final String key;
        /*占位符对应的数据*/
        private String value;

        private Placeholder(String prefix, String key) {
            this.prefix = prefix;
            this.key = key;
        }

        public static Placeholder newInstance(String prefix, String key) {
            return new Placeholder(prefix, key);
        }

        public static Placeholder newComma(String value) {
            if (StringUtils.isBlank(value))
                throw new CheckException("无效字段");
            int index = value.indexOf(".");
            String prefix = null;
            String key = value;
            if (index != -1) {
                prefix = value.substring(0, index);
                key = value.substring(index + 1, value.length());
            }
            return newInstance(prefix, key);
        }

        public String getPrefix() {
            return prefix;
        }

        public String getKey() {
            return key;
        }

        public String getKeyPrefix() {
            if (StringUtils.isNotBlank(this.prefix))
                return this.prefix + "." + this.getKey();
            return key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
