package wowjoy.fruits.ms.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Created by wangziwen on 2017/11/16.
 * 业务级日志记录AOP
 */
@Aspect
@Component
public class LogsAspectj {
    @Pointcut("within(wowjoy.fruits.ms.controller.*)")
    public void myWithin() {
    }

    @Pointcut("execution(public * insert*(..))")
    public void myExecutorSuffix() {
    }

    @Pointcut("execution(public * insert(..))")
    public void myExecutor() {
    }

    @Pointcut("myWithin() && (myExecutorSuffix() || myExecutor())")
    public void group() {
    }

    @Around("group()")
    public Object around(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
