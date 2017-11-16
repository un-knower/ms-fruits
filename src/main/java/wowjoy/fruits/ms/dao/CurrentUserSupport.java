package wowjoy.fruits.ms.dao;

import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

/**
 * Created by wangziwen on 2017/11/9.
 */
public interface CurrentUserSupport {
    /**
     * 获取当前登录用户
     *
     * @return
     */
    default FruitUser currentUser() {
        return ApplicationContextUtils.getCurrentUser();
    }
}
