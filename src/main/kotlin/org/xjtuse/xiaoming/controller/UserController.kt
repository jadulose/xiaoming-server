package org.xjtuse.xiaoming.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.xjtuse.xiaoming.model.User

@RestController
@RequestMapping("user")
class UserController {
    /**
     * 获取用户信息
     */
    @GetMapping("info")
    fun info(@AuthenticationPrincipal user: User) = user

    @PostMapping("login-code")
    fun loginCode(@RequestBody username: String) {
    }
}