package com.example.websockettouchin.websocket.service

import com.example.websockettouchin.websocket.dto.CommonMessage
import com.example.websockettouchin.websocket.dto.NewMessageEvent
import com.example.websockettouchin.websocket.dto.WebSocketEvent
import reactor.core.publisher.Mono
import java.util.*

interface ChatService {
    fun handleNewMessageEvent(senderId: UUID, newMessageEvent: NewMessageEvent): Mono<Void>
    fun markPreviousMessagesAsRead(messageId: UUID): Mono<Void>
    fun sendEventToUserId(userId: UUID, webSocketEvent: WebSocketEvent): Mono<Void>
    fun sendEventToAll(webSocketEvent: CommonMessage): Mono<Void>
    fun sendMessage(message: CommonMessage): Mono<Void>
    fun broadcastMessage(commonMessage: CommonMessage): Mono<Void>
}