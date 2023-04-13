package com.example.websockettouchin.redis

import org.slf4j.Logger
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class RedisListenerStarter(
    val logger: Logger,
    val redisChatMessageListener: RedisChatMessageListener
) {

    @Bean
    fun newMessageEventChannelListenerStarter(): ApplicationRunner {
        return ApplicationRunner { args: ApplicationArguments ->
            redisChatMessageListener.subscribeOnCommonMessageTopic()
                .doOnSubscribe { logger.info("Start NewMessageEvent channel listener") }
                .onErrorContinue { throwable, _ -> logger.error("Error occurred while listening NewMessageEvent channel", throwable) }
                .subscribe()
        }
    }
}