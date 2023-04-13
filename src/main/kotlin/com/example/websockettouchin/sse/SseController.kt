package com.example.websockettouchin.sse

import com.example.websockettouchin.websocket.config.SinkWrapper
import com.example.websockettouchin.websocket.dto.CommonMessage
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.time.Duration
import java.time.LocalTime


@RestController
@RequestMapping("/sse")
class SseController(
    val sinkWrapper: SinkWrapper,
    val objectMapper: ObjectMapper,
    val logger: Logger,
) {

    @GetMapping(path = ["/stream-flux"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamFlux(): Flux<ServerSentEvent<String>> {


        val resFlux: Flux<ServerSentEvent<String>> = Flux.create { fluxSink ->  // 3
            logger.info("create subscription ")

            fluxSink.onCancel{
                logger.info("subscription was closed")
            }

            sinkWrapper.unicastSink.asFlux()
                .doOnError { logger.error("SSE error ", it) }
                .map { message: CommonMessage ->
                    ServerSentEvent.builder<String>()
                        .id(message.messageId.toString())
                        // If other need in client change eventListner
                        .event("message")
                        .data(objectMapper.writeValueAsString(message))
                        .retry(Duration.ofSeconds(5))
                        .build()
                }.subscribe {
                    fluxSink.next(it)
                }

            fluxSink.next(
                ServerSentEvent.builder<String>()
                    .id("start")
                    // If other need in client change eventListner
                    .event("message")
                    .data("hello")
                    .retry(Duration.ofSeconds(5))
                    .build()
            )
        }

        return resFlux
    }

    @CrossOrigin
    @GetMapping("/stream")
    fun streamEvents(): Flux<ServerSentEvent<String>>? {
        return Flux.interval(Duration.ofSeconds(3))
            .map { sequence: Long ->
                ServerSentEvent.builder<String>()
                    .id(sequence.toString())
                    // If other need in client change eventListner
                    .event("message")
                    .data("SSE - " + LocalTime.now().toString())
                    .retry(Duration.ofSeconds(5))
                    .build()
            }
    }

}