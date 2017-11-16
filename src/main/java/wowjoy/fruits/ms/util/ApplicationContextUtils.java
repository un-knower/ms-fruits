package wowjoy.fruits.ms.util;

import org.apache.el.util.ConcurrentCache;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserVo;

/**
 * Created by wangziwen on 2017/8/24.
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {
    private static ThreadLocal<CurrentUser> currentUser = new ThreadLocal<>();
    private static ConcurrentCache<String, CurrentUser> userCache = new ConcurrentCache(10);
    private static ApplicationContext context;

    public ApplicationContextUtils() {
    }

    public static void setContext(ApplicationContext context) {
        ApplicationContextUtils.context = context;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static FruitUser getCurrentUser() {
        FruitUserVo vo = FruitUser.getVo();
        vo.setUserId("8401e45249434eafb7654447e02397a2");
        vo.setUserName("严老板");
        return vo;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setContext(applicationContext);
    }

    /**
     * 使用复合操作，利于扩展
     */
    private static class CurrentUser extends FruitUser {
        private FruitUser user;
        private String jwt;

        public FruitUser getUser() {
            return user;
        }

        public void setUser(FruitUser user) {
            this.user = user;
        }

        public String getJwt() {
            return jwt;
        }

        public void setJwt(String jwt) {
            this.jwt = jwt;
        }

        public static CurrentUser newInstance(FruitUser user) {
            CurrentUser result = new CurrentUser();
            result.setUser(user);
            return result;
        }
    }
}
