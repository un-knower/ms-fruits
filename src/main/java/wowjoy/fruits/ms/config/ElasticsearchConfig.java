package wowjoy.fruits.ms.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by wangziwen on 2017/9/22.
 */
@Configuration
public class ElasticsearchConfig {

    @Bean
    public TransportClient transportClient() {
        try {
            return new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("effiy.cn"), 9300));
        } catch (UnknownHostException e) {
            LoggerFactory.getLogger(this.getClass()).error(e.getMessage());
        }
        return null;
    }

}
