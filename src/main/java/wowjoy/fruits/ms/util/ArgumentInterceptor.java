package wowjoy.fruits.ms.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

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
        if (RequestMethod.GET.name().toLowerCase().equals(request.getMethod().toLowerCase()))
            return toType(findGet(request.getParameterMap()), parameter.getParameterAnnotation(JsonArgument.class).type());
        else
            return toType(findNotGet(request.getInputStream()), parameter.getParameterAnnotation(JsonArgument.class).type());
    }

    private <T> T toType(JsonElement parameter, Class<T> type) {
        if (Object.class.getName().equals(type.getName()))
            return (T) type;
        return gsonBuilder.fromJson(parameter, TypeToken.of(type).getType());

    }

    private JsonElement findNotGet(ServletInputStream inputStream) throws IOException {
        StringBuffer parameter = new StringBuffer();
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        while (inputStreamReader.ready())
            parameter.append((char) inputStreamReader.read());
        return new JsonParser().parse(parameter.toString());
    }

    private JsonObject findGet(Map<String, String[]> parameterMap) {
        final JsonObject result = new JsonObject();
        parameterMap.forEach((k, v) -> {
            if (v != null && v.length <= 1)
                result.addProperty(k, v[0]);
            else
                result.add(k, this.gsonBuilder.toJsonTree(v));
        });
        return result;
    }


}