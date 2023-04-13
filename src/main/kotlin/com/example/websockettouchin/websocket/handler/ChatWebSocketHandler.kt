package com.example.websockettouchin.websocket.handler

import com.example.websockettouchin.websocket.config.SinkWrapper
import com.example.websockettouchin.websocket.dto.ChatMember
import com.example.websockettouchin.websocket.dto.NewMessageEvent
import com.example.websockettouchin.websocket.repository.ChatRepository
import com.example.websockettouchin.websocket.service.ChatService
import com.example.websockettouchin.websocket.utils.ObjectStringConverter
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Claims
import org.slf4j.Logger
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.SynchronousSink
import java.time.Duration
import java.time.LocalDateTime.now
import java.util.*
import java.util.UUID.randomUUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.log

data class Event (
    val eventId: String? = "",
    val eventDt: String? = "",
)

@Component
class ChatWebSocketHandler(
    val objectMapper: ObjectMapper,
    val logger: Logger,
    val chatService: ChatService,
    val chatRepository: ChatRepository,
    val objectStringConverter: ObjectStringConverter,
    val sinkWrapper: SinkWrapper,
) : WebSocketHandler {

    private val userIdToSession: MutableMap<UUID, LinkedList<WebSocketSession>> = ConcurrentHashMap()

    override fun handle(session: WebSocketSession): Mono<Void> {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap { ctx ->
                val claims  = ctx.authentication.details as Claims
                val user = ChatMember(
                    userId = UUID.fromString(claims.subject.toString()),
                    fullName = claims["username"].toString(),
                )

                val sender = getSenderStream(session, user.userId)
                val receiver = getReceiverStream(session, user)

                return@flatMap Mono.zip(sender, receiver).then()
            }
    }

    private fun getReceiverStream(session: WebSocketSession, user: ChatMember): Mono<Void> {
        val userId = user.userId

        return session.receive()
            .filter { it.type == WebSocketMessage.Type.TEXT }
            .map(WebSocketMessage::getPayloadAsText)
            .flatMap {
                objectStringConverter.stringToObject(it, NewMessageEvent::class.java)
            }
            .flatMap { convertedEvent ->
                chatService.handleNewMessageEvent(userId, convertedEvent)
            }
            .onErrorContinue { t, _ -> logger.error("Error occurred with receiver stream", t) }
            .doOnSubscribe {
                val userSession = userIdToSession[userId]

                if (userSession == null) {
                    val newUserSessions = LinkedList<WebSocketSession>()
                    userIdToSession[userId] = newUserSessions
                    chatRepository.addMember(chatRepository.getFirst().chatId, user)
                }

                userIdToSession[userId]?.add(session)
            }
            .doFinally {
                val userSessions = userIdToSession[userId]
                userSessions?.remove(session)
            }
//            .log()
            .then()
    }

    private fun getSenderStream(session: WebSocketSession, userId: UUID): Mono<Void> {
        val sendMessage = sinkWrapper.sinks.asFlux()
            .filter { sendTo -> sendTo.userId == userId }
            .map { sendTo -> objectMapper.writeValueAsString(sendTo.event) }
            .map { stringObject -> session.textMessage(stringObject) }
//            .log()
            .doOnError { logger.error("Sender error ", it) }

        return session.send(sendMessage)
    }
}