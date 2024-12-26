package com.bizzan.bitrade.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/***
 这个 WebSocket 配置类的作用是：

 启用消息代理：配置消息代理，使得客户端可以订阅 /topic 开头的消息，并发送以 /app 开头的消息请求。
 配置 WebSocket 端点：设置 /swap-ws 为 WebSocket 服务的端点，客户端可以通过此 URL 建立连接。
 跨域支持：允许所有来源的客户端连接。
 回退机制：通过 SockJS 提供回退机制，以确保在不支持 WebSocket 的环境下也能正常工作。
 典型场景：
 客户端通过 WebSocket 与服务器进行实时通信。
 客户端订阅特定的消息频道（例如：/topic/someChannel），服务器向这些频道发送消息。
 客户端通过 /app/someRequest 路径向服务器发送请求消息，服务器会处理这些请求并回应客户端。
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/swap-ws").setAllowedOrigins("*").withSockJS();
    }


}