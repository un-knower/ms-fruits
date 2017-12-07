package wowjoy.fruits.ms.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import wowjoy.fruits.ms.exception.ExceptionSupport;
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
            return RestResult.newError(ex.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return RestResult.newError("后台发生无法处理的异常，联系开发人员");
        } catch (Exception ex) {
            ex.printStackTrace();
            String message = "";
            if (ex instanceof SQLException)
                message = ex.getMessage();
            return RestResult.newError(message);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
