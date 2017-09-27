package wowjoy.fruits.ms.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import wowjoy.fruits.ms.elastic.GlobalSearchEs;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

/**
 * Created by wangziwen on 2017/9/26.
 */
public class ProjectEsRunnable implements InterfaceEsRunnable {
    private final ProceedingJoinPoint joinPoint;

    private final GlobalSearchEs globalSearchEs;

    public ProjectEsRunnable(ProceedingJoinPoint joinPoint) {
        this.joinPoint = joinPoint;
        globalSearchEs = ApplicationContextUtils.getContext().getBean(GlobalSearchEs.class);
    }

    @Override
    public void run() {
        globalSearchEs.insert((AbstractEntity) joinPoint.getArgs()[0]);
    }
}
