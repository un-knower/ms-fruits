package wowjoy.fruits.ms.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by wangziwen on 2017/8/24.
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {
    private static ApplicationContext context;

    public static void setContext(ApplicationContext context) {
        ApplicationContextUtils.context = context;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setContext(applicationContext);
    }
}
