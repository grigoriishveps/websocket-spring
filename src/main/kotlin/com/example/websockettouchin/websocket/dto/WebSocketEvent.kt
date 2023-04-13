package com.example.websockettouchin.websocket.dto

import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.util.*

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE, include = JsonTypeInfo.As.PROPERTY)
sealed class WebSocketEvent

data class NewMessageEvent(val chatId: UUID, val content: String) : WebSocketEvent()
data class ChatMessageEvent(val chatId: UUID, val payload: CommonMessage) : WebSocketEvent()
data class MarkMessageAsRead(val chatId: UUID?, val messageId: UUID) : WebSocketEvent()
