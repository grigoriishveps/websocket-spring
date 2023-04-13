package com.example.websockettouchin.websocket.config;

import com.example.websockettouchin.websocket.dto.CommonMessage
import com.example.websockettouchin.websocket.dto.SendTo;
import reactor.util.concurrent.Queues.SMALL_BUFFER_SIZE
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

@Component
class SinkWrapper {
    val sinks: Sinks.Many<SendTo> = Sinks.many()
        .multicast()
        .onBackpressureBuffer(SMALL_BUFFER_SIZE , false)
    val unicastSink: Sinks.Many<CommonMessage> = Sinks.many()
        .multicast()
        .onBackpressureBuffer(SMALL_BUFFER_SIZE , false)
}
