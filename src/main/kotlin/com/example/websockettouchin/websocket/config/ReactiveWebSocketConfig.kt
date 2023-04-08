package com.example.websockettouchin.websocket.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerMapping
import org.springframework.web.socket.WebSocketHandler

@Configuration
class ReactiveWebSocketConfig {

    @Bean
    fun webSocketHandlerMapping(chatWebSocketHandler: ChatWebSocketHandler): HandlerMapping {
        val map: MutableMap<String, WebSocketHandler> = HashMap()
        map["/ws/chat"] = chatWebSocketHandler

        val handlerMapping = SimpleUrlHandlerMapping()
        handlerMapping.setCorsConfigurations(Collections.singletonMap("*", CorsConfiguration().applyPermitDefaultValues()))
        handlerMapping.order = 1
        handlerMapping.urlMap = map
        return handlerMapping
    }

    @Bean
    fun handlerAdapter(): WebSocketHandlerAdapter {
        return WebSocketHandlerAdapter()
    }
}