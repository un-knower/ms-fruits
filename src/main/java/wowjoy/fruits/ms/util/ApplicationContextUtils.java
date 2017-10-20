package wowjoy.fruits.ms.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import wowjoy.fruits.ms.module.user.FruitUser;

/**
 * Created by wangziwen on 2017/8/24.
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {
    private static ThreadLocal<FruitUser> currentUser = new ThreadLocal<>();
    private static ApplicationContext context;

    public static void setContext(ApplicationContext context) {
        ApplicationContextUtils.context = context;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static FruitUser getCurrentUser() {
        return currentUser.get();
    }

    static void setCurrentUser(FruitUser currentUser) {
        ApplicationContextUtils.currentUser.set(currentUser);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setContext(applicationContext);
    }
}
