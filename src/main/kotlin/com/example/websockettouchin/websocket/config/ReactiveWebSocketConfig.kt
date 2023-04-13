package com.example.websockettouchin.websocket.config

import com.example.websockettouchin.websocket.handler.ChatWebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import java.util.*
import java.util.Map

@Configuration
class ReactiveWebSocketConfig(
    private val chatWebSocketHandler: ChatWebSocketHandler
) {

    @Bean
    fun webSocketHandlerMapping(): HandlerMapping {
        val map = Map.of<String, WebSocketHandler>("/ws/chat", chatWebSocketHandler)
        val handlerMapping = SimpleUrlHandlerMapping(map, 1)

        handlerMapping.setCorsConfigurations(Collections.singletonMap("*", CorsConfiguration().applyPermitDefaultValues()))

        return handlerMapping
    }

    @Bean
    fun handlerAdapter(): WebSocketHandlerAdapter {
        return WebSocketHandlerAdapter()
    }

}