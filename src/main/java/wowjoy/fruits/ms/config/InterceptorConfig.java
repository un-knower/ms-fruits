package wowjoy.fruits.ms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import wowjoy.fruits.ms.util.ArgumentInterceptor;

import java.util.List;

/**
 * Created by wangziwen on 2017/8/30.
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new ArgumentInterceptor());
    }
}
