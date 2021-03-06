package wowjoy.fruits.ms.config;

import com.google.gson.*;
import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import wowjoy.fruits.ms.util.ArgumentInterceptor;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by wangziwen on 2017/8/30.
 */
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new ArgumentInterceptor());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        gsonHttpMessageConverter.setGson(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
//                .registerTypeAdapter(LocalDate.class, LocalDateAdapter.newInstance())
                .create());
        converters.add(gsonHttpMessageConverter);
    }

    /**
     * 时间转换字段工具
     */
    private static class LocalDateAdapter implements JsonSerializer<LocalDate> {

        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        public static LocalDateAdapter getInstance() {
            return new LocalDateAdapter();
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
    }

}