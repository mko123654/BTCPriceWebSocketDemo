package com.abby.websocket.config;

import com.abby.websocket.intecepter.HttpHandShakeInterceptor;
import com.abby.websocket.intecepter.SocketChannelInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author Abby Chang
 * @Description: @EnableWebSocketMessageBroker 用於開啟使用STOMP協議來傳輸基於代理（MessageBroker）的消息，這時候controller
 * 開始支援websocket的@MessageMapping，就像是使用RESTful的@requestMapping那樣。
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * 註冊客戶端連進Server端之路徑
     * 並使用攔截器設定可以連進來的來源位置(這邊範例使用 "*" 都不擋)
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/endpoint-websocket").addInterceptors(new HttpHandShakeInterceptor()).setAllowedOrigins("*").withSockJS();
    }

    /**
     * 配置訊息代理
     * enableSimpleBroker server端推送給client端的路徑prefix
     * setApplicationDestinationPrefixes  client端發送給server端的路徑prefix
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");

    }

    /**
     * 註冊由client端傳到server端的攔截器
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new SocketChannelInterceptor());
    }

    /**
     * 註冊由server端傳到client端的攔截器
     */
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(new SocketChannelInterceptor());
    }

}
