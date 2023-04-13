package com.example.websockettouchin.websocket.dto

import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet

class Chat {
    val chatId: UUID = UUID.randomUUID()
    private val chatMembers: MutableSet<ChatMember> = CopyOnWriteArraySet()
    val createdDate: LocalDateTime = LocalDateTime.now()

    fun addMember(member: ChatMember): Boolean {
        return chatMembers.add(member)
    }

    fun getChatMembers(): Set<ChatMember>  {
        return chatMembers.toSet()
    }

}
