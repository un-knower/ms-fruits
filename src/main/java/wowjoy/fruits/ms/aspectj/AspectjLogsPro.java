package wowjoy.fruits.ms.aspectj;

import com.google.gson.Gson;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
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

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by wangziwen on 2018/3/12.
 */
@Aspect
@Component
@Order(1)
public class AspectjLogsPro implements InterfaceDao {
    private final ServiceLogs serviceLogs;

    @Autowired
    public AspectjLogsPro(ServiceLogs serviceLogs) {
        this.serviceLogs = serviceLogs;
    }

    @Pointcut("@annotation(wowjoy.fruits.ms.aspectj.LogInfo)")
    public void myAnnotation() {
    }

    @Around("myAnnotation() && @annotation(annotation)")
    public Object around(ProceedingJoinPoint point, LogInfo annotation) {
        try {
            Object result = point.proceed();
            if (((RestResult) result).getSuccess()) {
                FruitUser currentUser = ApplicationContextUtils.getCurrentUser();
                DaoThread.getFixed().execute(() -> {
                    try {
                        logRecord(point, annotation, currentUser);
                    } catch (Throwable throwable) {
                        logger.error(throwable.getMessage());
                    }
                    return true;
                });
            }
            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    /**
     * 获取uuid
     *
     * @param point
     * @param annotation
     * @param currentUser
     */
    public void logRecord(ProceedingJoinPoint point, LogInfo annotation, FruitUser currentUser) throws Throwable {
        Optional<? extends AbstractEntity> currentData = LogsWriteTemplate.getTypeFunction(annotation.type(), obtainOperateType(point).orElseGet(annotation::operateType)).apply(this.uuid(point).orElseThrow(() -> {
            throw new CheckException("【日志记录】无法获取uuid");
        }));
        currentData.orElseThrow(() -> {
            throw new CheckException("【日志记录】未找到最新保存的数据，日志记录失败");
        });

        serviceLogs.insertVo(logsVo -> {
            logsVo.setJsonObject(new Gson().toJsonTree(currentData.get()).toString());
            logsVo.setVoObject(new Gson().toJsonTree(this.obtainArgByAnnotation(point, JsonArgument.class, AbstractEntity.class).orElse(null)).toString());
            logsVo.setFruitType(annotation.type());
            logsVo.setOperateType(this.obtainOperateType(point).orElseGet(annotation::operateType));
            logsVo.setUserId(currentUser.getUserId());
            /*获取对应的uuid*/
            logsVo.setFruitUuid(currentData.get().getUuid());
        });
    }

    /**
     * 获取动态操作类型
     *
     * @param point
     * @return
     */
    private Optional<FruitDict.LogsDict> obtainOperateType(ProceedingJoinPoint point) {
        Optional<AbstractEntity> abstractEntity = this.obtainArgByAnnotation(point, JsonArgument.class, AbstractEntity.class);
        if (abstractEntity.isPresent() && abstractEntity.get().getOperateTypeSupplier() != null)
            return Optional.ofNullable(abstractEntity.get().getOperateTypeSupplier().get());
        return Optional.empty();
    }

    /**
     * 获取记录UUID
     * 优先使用PathVariable注解绑定UUID
     * 若PathVariable注解不存在，使用JsonArgument注解UUID
     *
     * @param point
     * @return
     */
    private Optional<String> uuid(ProceedingJoinPoint point) {
        Optional<Method> method = Stream.of(point.getSignature().getDeclaringType().getMethods())
                .filter(methodName -> methodName.getName().equals(point.getSignature().getName())) /*过滤出当前使用中的函数*/
                .findAny();/*使用随机获取一个方法。需要考虑以后方法重载的问题*/
        if (!method.isPresent()) return Optional.empty();
        Optional<String> pathVariable = this.obtainArgByAnnotation(point, PathVariable.class, String.class);
        if (pathVariable.isPresent())
            return Optional.ofNullable(Objects.toString(pathVariable.get()));
        Optional<AbstractEntity> jsonArgument = this.obtainArgByAnnotation(point, JsonArgument.class, AbstractEntity.class);
        if (jsonArgument.isPresent())
            return Optional.ofNullable(jsonArgument.get().getUuid());
        return Optional.empty();
    }

    /*获取指定参数注解所代表的参数*/
    private <T> Optional<T> obtainArgByAnnotation(ProceedingJoinPoint point, Class annotationClass, Class<T> tClass) {
        Optional<T> result = Optional.empty();
        Optional<Method> method = Stream.of(point.getSignature().getDeclaringType().getMethods())
                .filter(methodName -> methodName.getName().equals(point.getSignature().getName())).findAny(); /*过滤出当前使用中的函数*/
        if (!method.isPresent()) return Optional.empty();
        for (int i = 0; i < method.get().getParameterAnnotations().length; i++) {
            if (Optional.ofNullable(method.get().getParameterAnnotations()[i][0]).filter(annotation -> annotation.annotationType().getName().equals(annotationClass.getName())).isPresent())
                result = Optional.ofNullable((T) point.getArgs()[i]);
        }
        return result;
    }
}
