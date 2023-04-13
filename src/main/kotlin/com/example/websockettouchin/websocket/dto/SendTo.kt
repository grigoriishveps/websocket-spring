package com.example.websockettouchin.websocket.dto

import java.util.*

data class SendTo(
    val userId: UUID,
    val event: WebSocketEvent
)
