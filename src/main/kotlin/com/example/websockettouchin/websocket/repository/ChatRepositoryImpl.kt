package com.example.websockettouchin.websocket.repository

import com.example.websockettouchin.websocket.dto.Chat
import com.example.websockettouchin.websocket.dto.ChatMember
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Repository
class ChatRepositoryImpl : ChatRepository {
    final val chats = ConcurrentHashMap<UUID, Chat>()

    init {
        val chat = Chat()
        chats[chat.chatId] = chat
    }

    override fun save(chat: Chat): Mono<Chat> {
//        chats[chat.chatId] = chat
        return Mono.just(getFirst())
    }

    override fun findById(id: UUID): Mono<Chat> {
//        return Mono.justOrEmpty(chats[id])
        return Mono.justOrEmpty(getFirst())
    }

    override fun addMember(chatId: UUID, member: ChatMember): Boolean {
        return chats[chatId]
            ?.addMember(member)
            ?: false
    }

    override fun getFirst(): Chat {
        return chats.values.first()
    }

}