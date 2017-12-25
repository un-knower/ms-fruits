package wowjoy.fruits.ms.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by wangziwen on 2017/8/30.
 */
public class ArgumentInterceptor implements HandlerMethodArgumentResolver {
    private final Gson gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JsonArgument.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        final String data = RequestMethod.GET.name().toLowerCase().equals(request.getMethod().toLowerCase()) ? findGet(request.getParameterMap()) : findNotGet(request.getInputStream());
        try {
            return toType(data, parameter.getParameterAnnotation(JsonArgument.class).type());
        } catch (ExceptionSupport ex) {
            throw new CheckException(ex.getMessage());
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new CheckException("入参转换发生未知的异常，请联系开发人员");
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
            throw new CheckException("参数不符合设计需求，请联系接口开发人员核对接口入参");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new CheckException("创建默认实例失败");
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new CheckException("创建默认实例失败");
        }
    }

    private String findNotGet(ServletInputStream inputStream) throws IOException {
        StringBuffer parameter = new StringBuffer();
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        while (inputStreamReader.ready())
            parameter.append((char) inputStreamReader.read());
        return parameter.toString();
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
