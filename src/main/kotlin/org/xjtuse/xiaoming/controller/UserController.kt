package org.xjtuse.xiaoming.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.xjtuse.xiaoming.model.LoginInfo.UsernameType
import org.xjtuse.xiaoming.model.User
import org.xjtuse.xiaoming.service.UserService
import org.xjtuse.xiaoming.service.impl.MailService

@RestController
@RequestMapping("user")
class UserController @Autowired constructor(
    private var userService: UserService,
    private var mailService: MailService,
) {
    /**
     * 获取用户信息
     */
    @GetMapping("info")
    fun info(@AuthenticationPrincipal user: User) = user

    @PostMapping("login-code")
    fun loginCode(@RequestBody username: String) {
        // TODO 异常处理&防止多次尝试
        try {
            val user = userService.find(username, UsernameType.EMAIL)
            mailService.sendLoginCode(user)
        } catch (_: UsernameNotFoundException) {
        }
    }
}