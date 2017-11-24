package wowjoy.fruits.ms.module.user;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wangziwen on 2017/9/14.
 */
public class FruitUserVo extends FruitUser {
    protected FruitUserVo() {
    }

    private String principal;

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    @Override
    public String getUserId() {
        return super.getUserId();
    }
}
