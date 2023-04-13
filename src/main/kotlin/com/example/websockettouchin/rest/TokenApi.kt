package com.example.websockettouchin.rest

import com.example.websockettouchin.websocket.utils.JwtUtil
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class TokenApi(
    private val jwtUtil: JwtUtil
) {
    @GetMapping("/actuator/token")
    @ResponseBody
    fun dynamicBuilderSpecific(@RequestParam username: String): String {
        return jwtUtil.getToken(username)
    }
}

