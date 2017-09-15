package wowjoy.fruits.ms.module.user;

import org.apache.commons.lang.StringUtils;

/**
 * Created by wangziwen on 2017/9/14.
 */
public class FruitUserVo extends FruitUser {
    protected FruitUserVo() {
    }

    @Override
    public String getUserId() {
        if (StringUtils.isBlank(super.getUserId()))
            throw new NullEntityException("用户id不能为空");
        return super.getUserId();
    }
}
