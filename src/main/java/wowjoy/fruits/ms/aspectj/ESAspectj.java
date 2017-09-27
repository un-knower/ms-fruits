package wowjoy.fruits.ms.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangziwen on 2017/9/22.
 */
@Aspect
@Component
public class ESAspectj {
    private final Logger logger = LoggerFactory.getLogger(ESAspectj.class);
    private final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * 切入点-注解
     */
    @Pointcut("@annotation(wowjoy.fruits.ms.aspectj.ESSynchro)")
    public void pointcutAnnotaion() {
    }

    @Around(value = "pointcutAnnotaion() && @annotation(argument)")
    public void around(ProceedingJoinPoint joinPoint, ESSynchro argument) throws Throwable {
        joinPoint.proceed();
        /*仅启动多线程，不阻塞主线层执行*/
        run(argument.tClass(), joinPoint);
    }

    public void run(Class<? extends InterfaceEsRunnable> zlass, ProceedingJoinPoint joinPoint) {
        try {
            InterfaceEsRunnable globalSearch = (InterfaceEsRunnable) Class.forName(zlass.getName()).getConstructor(ProceedingJoinPoint.class).newInstance(joinPoint);
            threadPool.execute(globalSearch);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
