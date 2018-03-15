package wowjoy.fruits.ms.aspectj;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.dao.logs.service.ServiceLogs;
import wowjoy.fruits.ms.dao.logs.template.LogsWriteTemplate;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;
import wowjoy.fruits.ms.util.JsonArgument;
import wowjoy.fruits.ms.util.RestResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by wangziwen on 2017/11/16.
 * 业务级日志记录AOP
 */
//@Aspect
//@Component
//@Order(1)
public class LogsAspectj {

    private final ServiceLogs logsDao;

    private static final InterfaceDao.DaoThread daoThread = InterfaceDao.DaoThread.getFixed();

    @Autowired
    public LogsAspectj(ServiceLogs logsDao) {
        this.logsDao = logsDao;
    }

    @Pointcut("@annotation(wowjoy.fruits.ms.aspectj.LogInfo)")
    public void myAnnotation() {
    }

    @Around("myAnnotation() && @annotation(annotation)")
    public Object around(ProceedingJoinPoint joinPoint, LogInfo annotation) {
        try {
            Object result = joinPoint.proceed();
            if (((RestResult) result).getSuccess()) {
                FruitUser currentUser = ApplicationContextUtils.getCurrentUser();
                daoThread.execute(() -> {
                    record(joinPoint, annotation, currentUser);
                    return true;
                });
            }
            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    /*记录日志*/
    public void record(ProceedingJoinPoint joinPoint, LogInfo logInfo, FruitUser currentUser) {
        /*根据uuid查询数据库中保存数据*/
        Function<Class, Optional<AnnotationValue<Annotation, AbstractEntity>>> annotationFunction = (annotation) -> Optional.ofNullable(getAnnotation(joinPoint, annotation));
        Optional<? extends AbstractEntity> DBData = LogsWriteTemplate.getTypeFunction(logInfo.type(), logInfo.operateType()).apply((String) getValue(annotationFunction, Placeholder.newComma(logInfo.uuid())));
        if (!DBData.isPresent()) return;
        Gson gson = new Gson();
        Optional<AnnotationValue<Annotation, AbstractEntity>> enterArgument = annotationFunction.apply(JsonArgument.class);
        /*记录日志*/
        logsDao.insertVo(vo -> {
            vo.setJsonObject(gson.toJsonTree(DBData.get()).toString());
            if (enterArgument.isPresent())
                vo.setVoObject(gson.toJsonTree(enterArgument.get().getValue()).toString());
            vo.setFruitType(logInfo.type());
            vo.setOperateType(this.operateType(annotationFunction, logInfo));
            vo.setUserId(currentUser.getUserId());
            /*获取对应的uuid*/
            vo.setFruitUuid(DBData.get().getUuid());
        });
    }

    public FruitDict.LogsDict operateType(Function<Class, Optional<AnnotationValue<Annotation, AbstractEntity>>> annotationOptionalFunction, LogInfo logInfo) {
        Optional<AnnotationValue<Annotation, AbstractEntity>> annotationValue = annotationOptionalFunction.apply(JsonArgument.class);
        if (annotationValue.isPresent() && annotationValue.get().getValue().getOperateTypeSupplier() != null)
            return annotationValue.get().getValue().getOperateTypeSupplier().get();
        return logInfo.operateType();
    }

    private Object getValue(Function<Class, Optional<AnnotationValue<Annotation, AbstractEntity>>> annotationFunction, Placeholder placeholder) {
        Optional<Object> result = Optional.empty();
        Predicate<Optional<AnnotationValue>> pathVariable = annotationValue -> annotationValue.isPresent()
                && ((AnnotationValue<PathVariable, String>) annotationValue.get()).getAnnotation().value().equals(placeholder.getKey());
//        if (pathVariable.test(annotationFunction.apply(PathVariable.class)))
//            result = Optional.ofNullable(annotationFunction.apply(PathVariable.class).get().getValue());
//        if (!result.isPresent() && annotationFunction.apply(JsonArgument.class).isPresent()) {
//            AnnotationValue jsonArgument = annotationFunction.apply(PathVariable.class).get();
//            result = Optional.ofNullable(getValue((AbstractEntity) jsonArgument.getValue(), jsonArgument.getValue().getClass(), placeholder));
//        }
        return result;
    }

    private Object getValue(AbstractEntity arg, Class aClass, Placeholder placeholder) {
        for (Method method : aClass.getDeclaredMethods()) {
            if (!method.getName().toLowerCase().equals("get" + placeholder.getKey().toLowerCase())) continue;
            try {
                return method.invoke(arg, null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new CheckException("获取失败");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                throw new CheckException("获取失败");
            }
        }
        if (aClass.getSuperclass().getName().equals(Object.class.getName())) return null;
        return getValue(arg, aClass.getSuperclass(), placeholder);
    }

    private <T> AnnotationValue getAnnotation(ProceedingJoinPoint joinPoint, Class<T> annotation) {
        for (Method method : joinPoint.getSignature().getDeclaringType().getMethods()) {
            if (!method.getName().equals(joinPoint.getSignature().getName())) continue;
            for (int i = 0; i < method.getParameterAnnotations().length; i++) {
                if (method.getParameterAnnotations()[i][0].annotationType().getName().equals(annotation.getName()))
                    return new AnnotationValue(method.getParameterAnnotations()[i][0], joinPoint.getArgs()[i]);
            }
        }
        return null;
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

    private static class AnnotationValue<T, R> {
        private final T annotation;
        private final R value;

        private AnnotationValue(T annotation, R value) {
            this.annotation = annotation;
            this.value = value;
        }

        public T getAnnotation() {
            return annotation;
        }

        public R getValue() {
            return value;
        }
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
