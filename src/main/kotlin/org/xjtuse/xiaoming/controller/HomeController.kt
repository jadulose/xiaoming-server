package org.xjtuse.xiaoming.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.xjtuse.xiaoming.util.Logger
import javax.servlet.http.HttpServletRequest

@RestController
class HomeController {
    companion object : Logger()

    @GetMapping("hello")
    fun hello(request: HttpServletRequest): String {
        log.info("收到了来自${request.remoteAddr}的问候")
        return "你好，世界"
    }

    @GetMapping("user")
    fun user(@AuthenticationPrincipal user: User) = user
}