package wowjoy.fruits.ms.aspectj;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.MessageException;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.RestResult;

import java.sql.SQLException;

/**
 * Created by wangziwen on 2017/9/29.
 */

@Aspect
@Component
@Order(2)
public class ExceptionAspectj {
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void pointcut() {
    }

    @Around(value = "pointcut() && @annotation(annotation)")
    public Object around(ProceedingJoinPoint joinPoint, RequestMapping annotation) {
        return exception(joinPoint);
    }

    private Object exception(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (ExceptionSupport ex) {
            if (ex instanceof CheckException) {
                FruitDict.Exception.Check check = FruitDict.Exception.Check.valueOf(ex.getMessage());
                return RestResult.newError(403, check.name(), check.getValue());
            }
            if (ex instanceof MessageException)
                return RestResult.newError(412, "服务器驳回请求", () -> {
                    try {
                        return new JsonParser().parse(ex.getMessage());
                    } catch (JsonSyntaxException jex) {
                        return ex.getMessage();
                    }
                });
        } catch (RuntimeException e) {
            e.printStackTrace();
            return RestResult.newError(500, "发生了未捕获的异常，等待处理");
        } catch (Exception ex) {
            ex.printStackTrace();
            String message = "";
            if (ex instanceof SQLException)
                message = ex.getMessage();
            return RestResult.newError(500, message);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
