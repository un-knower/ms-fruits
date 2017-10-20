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
            String header = request.getHeader(headerKey);
            String[] headers = header.split(" ");
            if (headers.length <= 1 || StringUtils.isBlank(headers[1]))
                throw new CheckException("非法请求");
            JwtUtils.Jwt jwt = JwtUtils.newJwt(header);
            if (!jwt.checkSignature(header))
                throw new CheckException("token被攻击，拒绝请求");
            FruitUser user = FruitUser.getInstance();
            user.setUserId(jwt.getPayload().getUserId());
            ApplicationContextUtils.setCurrentUser(user);
        } catch (Exception ex) {
            throw new CheckException("验证授权失败：" + ex.getMessage());
        }
        return true;
    }

}
