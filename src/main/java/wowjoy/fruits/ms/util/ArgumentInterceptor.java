package wowjoy.fruits.ms.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import sun.plugin2.message.Message;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.MessageException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by wangziwen on 2017/8/30.
 */
public class ArgumentInterceptor implements HandlerMethodArgumentResolver {
    private final Gson gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JsonArgument.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        final String data = RequestMethod.GET.name().toLowerCase().equals(request.getMethod().toLowerCase()) ? findGet(request.getParameterMap()) : findNotGet(request);
        try {
            return toType(data, parameter.getParameterAnnotation(JsonArgument.class).type());
        } catch (ExceptionSupport ex) {
            throw new MessageException(ex.getMessage());
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new MessageException("入参转换发生未知的异常，请联系开发人员");
        }
    }

    private <T> T toType(String parameter, Class<T> type) {
        if (Object.class.getName().equals(type.getName()))
            return (T) type;
        try {
            T obj = gsonBuilder.fromJson(parameter, TypeToken.of(type).getType());
            if (obj == null)
                return type.newInstance();
            return obj;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new MessageException("参数不符合设计需求，请联系接口开发人员核对接口入参");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new MessageException("创建默认实例失败");
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new MessageException("创建默认实例失败");
        }
    }

    private String findNotGet(HttpServletRequest request) throws IOException {
        StringBuffer parameter = new StringBuffer();
        ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(request);
        Reader reader = new InputStreamReader(inputMessage.getBody(), getCharset(inputMessage.getHeaders()));
        int ch;
        while ((ch = reader.read()) != -1) {
            parameter.append((char) ch);
        }
        return parameter.toString();
    }

    private Charset getCharset(HttpHeaders headers) {
        if (headers == null || headers.getContentType() == null || headers.getContentType().getCharset() == null) {
            return DEFAULT_CHARSET;
        }
        return headers.getContentType().getCharset();
    }

    private String findGet(Map<String, String[]> parameterMap) {
        final JsonObject result = new JsonObject();
        parameterMap.forEach((k, v) -> {
            if (v != null && v.length <= 1)
                result.addProperty(k, v[0]);
            else
                result.add(k, this.gsonBuilder.toJsonTree(v));
        });
        return result.toString();
    }


}
