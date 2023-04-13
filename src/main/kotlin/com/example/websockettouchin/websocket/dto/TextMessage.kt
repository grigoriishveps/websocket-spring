package com.example.websockettouchin.websocket.dto

import java.time.LocalDateTime
import java.util.*

class TextMessage(
    messageId: UUID,
    chatId: UUID,
    sender: ChatMember,
    content: String,
    messageDate: LocalDateTime,
    seen: Boolean
) : CommonMessage(messageId, chatId, sender, content, messageDate, seen)