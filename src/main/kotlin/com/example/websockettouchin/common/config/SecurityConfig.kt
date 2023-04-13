package com.example.websockettouchin.common.config

import com.example.websockettouchin.websocket.security.SecurityContextRepository
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*


@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(
    val reactiveAuthenticationManager: ReactiveAuthenticationManager,
    val securityContextRepository: SecurityContextRepository,
) {

    @Bean
    fun securityWebFilterChain(httpSecurity: ServerHttpSecurity): SecurityWebFilterChain {
        return httpSecurity
            .exceptionHandling()
            .authenticationEntryPoint { swe: ServerWebExchange, e: AuthenticationException ->
                Mono.fromRunnable { swe.response.statusCode = HttpStatus.UNAUTHORIZED }
            }
            .accessDeniedHandler { swe: ServerWebExchange, e: AccessDeniedException? ->
                Mono.fromRunnable { swe.response.statusCode = HttpStatus.FORBIDDEN }
            }
            .and()
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .authenticationManager(reactiveAuthenticationManager)
            .securityContextRepository(securityContextRepository)
            .authorizeExchange()
            .pathMatchers("/actuator/**").permitAll()
            .pathMatchers("/sse/**").permitAll()
            .pathMatchers(HttpMethod.GET, "/ws/**").hasAuthority("ROLE_USER")
            .anyExchange().authenticated()
            .and()
            .build()
    }

    @Bean
    fun corsConfiguration(): CorsConfigurationSource {
        val corsConfig = CorsConfiguration()
        val source = UrlBasedCorsConfigurationSource()

        corsConfig.applyPermitDefaultValues()
        corsConfig.addAllowedMethod("GET")
        corsConfig.addAllowedMethod("POST")
        source.registerCorsConfiguration("/**", corsConfig)

        return source
    }

}