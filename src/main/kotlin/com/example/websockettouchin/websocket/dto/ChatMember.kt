package com.example.websockettouchin.websocket.dto

import java.util.*
data class ChatMember(
    val userId: UUID,
    var fullName: String,
    var deletedChat: Boolean
)
