package wowjoy.fruits.ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Created wangziwen on 2017/8/20.
 */

/**
 * 　　　　　　　 ┏┓       ┏┓+ +
 * 　　　　　　　┏┛┻━━━━━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　 ┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 █████━█████  ┃+
 * 　　　　　　　┃　　　　　　 ┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　 ┃ + +
 * 　　　　　　　┗━━┓　　　 ┏━┛
 *                 ┃　　   ┃
 * 　　　　　　　　　┃　　  ┃ + + + +
 * 　　　　　　　　　┃　　　┃　Code is far away from     bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ + 　　　　         神兽保佑,代码无bug
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　 ┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━━━┳┓┏┛ + + + +
 * 　　　　　　　　　 ┃┫┫　 ┃┫┫
 * 　　　　　　　　　 ┗┻┛　 ┗┻┛+ + + +
 *
 *       ┏┛ ┻━━━━━┛ ┻┓
 *       ┃　　　　　　 ┃
 *       ┃　　　━　　　┃
 *       ┃　┳┛　  ┗┳　┃¬
 *       ┃　　　　　　 ┃
 *       ┃　　　┻　　　┃
 *       ┃　　　　　　 ┃
 *       ┗━┓　　　┏━━━┛
 *         ┃　　　┃   神兽保佑
 *         ┃　　　┃   代码无BUG！
 *         ┃　　　┗━━━━━━━━━┓
 *         ┃　　　　　　　    ┣┓
 *         ┃　　　　         ┏┛
 *         ┗━┓ ┓ ┏━━━┳ ┓ ┏━┛
 *           ┃ ┫ ┫   ┃ ┫ ┫
 *           ┗━┻━┛   ┗━┻━┛
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableConfigurationProperties
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RestTemplate restaTemplate() {
        return new RestTemplate();
    }

}