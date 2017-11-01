package wowjoy.fruits.ms.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import wowjoy.fruits.ms.util.RestResult;

/**
 * Created by wangziwen on 2017/9/29.
 */

@Aspect
@Component
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
        } catch (RuntimeException e) {
            e.printStackTrace();
            return RestResult.newError(e.getMessage());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
