package wowjoy.fruits.ms.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.user.FruitUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wangziwen on 2017/10/19.
 */
public class TokenInterceptor extends HandlerInterceptorAdapter {
    private final String headerKey = "Authorization";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String[] headers = request.getHeader(headerKey).split(" ");
            if (headers.length <= 1 || StringUtils.isBlank(headers[1]))
                throw new CheckException("Authorization not null");
            String header = headers[1];
            FruitUser user = FruitUser.getInstance();
            user.setUserId(JwtUtils.newJwt(header).getPayload().getUserId());
            ApplicationContextUtils.setCurrentUser(user);
        } catch (Exception ex) {
            throw new CheckException("Authorization exception：" + ex.getMessage());
        }
        return true;
    }

}
