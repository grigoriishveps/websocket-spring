package com.example.websockettouchin.websocket.repository

import com.example.websockettouchin.websocket.dto.Chat
import com.example.websockettouchin.websocket.dto.ChatMember
import reactor.core.publisher.Mono
import java.util.*

interface ChatRepository {
    fun findById(id: UUID): Mono<Chat>
    fun save(chat: Chat): Mono<Chat>
    fun addMember(chatId: UUID, member: ChatMember): Boolean
    fun getFirst(): Chat
}