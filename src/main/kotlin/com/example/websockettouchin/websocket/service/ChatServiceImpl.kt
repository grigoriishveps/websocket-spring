package com.example.websockettouchin.websocket.service

import com.example.websockettouchin.redis.RedisChatMessagePublisher
import com.example.websockettouchin.websocket.config.SinkWrapper
import com.example.websockettouchin.websocket.dto.*
import com.example.websockettouchin.websocket.repository.ChatRepository
import org.slf4j.Logger
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.time.LocalDateTime
import java.util.*

@Service
class ChatServiceImpl(
    val logger: Logger,
    val sinkWrapper: SinkWrapper,
    val chatRepository: ChatRepository,
    val redisChatPublisher: RedisChatMessagePublisher
) : ChatService {

    override fun handleNewMessageEvent(senderId: UUID, newMessageEvent: NewMessageEvent): Mono<Void> {
        logger.info("Receive NewMessageEvent from $senderId: $newMessageEvent")

        return chatRepository.findById(newMessageEvent.chatId)
            .filter { it.getChatMembers().map(ChatMember::userId).contains(senderId) }
            .flatMap { chat ->
                val textMessage = TextMessage(
                    messageId = UUID.randomUUID(),
                    chatId = chat.chatId,
                    sender = chat.getChatMembers().first { it.userId == senderId },
                    content = newMessageEvent.content,
                    messageDate = LocalDateTime.now(),
                    seen = false
                )

                return@flatMap Mono.zip(chatRepository.save(chat), Mono.just(textMessage))
            }
            .flatMap {


                return@flatMap Mono.zip(
                    broadcastMessage(it.t2),
                    sendEventToAll(it.t2),
                ).then()
            }
    }

    /**
     * Broadcast the message between instances
     */
    override fun broadcastMessage(commonMessage: CommonMessage): Mono<Void> {
        return redisChatPublisher.broadcastMessage(commonMessage)
    }

    /**
     * Send the message to all of chatMembers of message chat direct
     */
    override fun sendMessage(message: CommonMessage): Mono<Void> {
        return chatRepository.findById(message.chatId)
            .map { it.getChatMembers() }
            .flatMapMany { Flux.fromIterable(it) }
            .filter { it.userId != message.sender.userId }
            .flatMap { member -> sendEventToUserId(member.userId, ChatMessageEvent(message.chatId, message)) }
//            .log()
            .then()
    }

    override fun markPreviousMessagesAsRead(messageId: UUID): Mono<Void> {
        TODO("Not yet implemented")
    }

    override fun sendEventToUserId(userId: UUID, webSocketEvent: WebSocketEvent): Mono<Void> {
        return Mono.fromCallable {
            sinkWrapper.sinks.emitNext(
                SendTo(userId, webSocketEvent),
                Sinks.EmitFailureHandler.FAIL_FAST
            )
        }
            .then()
    }

    override fun sendEventToAll(webSocketEvent: CommonMessage): Mono<Void> {
        return Mono.fromCallable {
            sinkWrapper.unicastSink.emitNext(
                webSocketEvent,
                Sinks.EmitFailureHandler.FAIL_FAST
            )
        }
            .then()
    }
}