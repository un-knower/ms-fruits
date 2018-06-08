package wowjoy.fruits.ms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import wowjoy.fruits.ms.websocket.MsgHandler;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(msgWebSocket(), "/system_msg")
        .addInterceptors(new HttpSessionHandshakeInterceptor());
    }

    @Bean
    public MsgHandler msgWebSocket() {
        return new MsgHandler();
    }

}
