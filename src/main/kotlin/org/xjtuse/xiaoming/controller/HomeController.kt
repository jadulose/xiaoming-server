package org.xjtuse.xiaoming.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController {
    @GetMapping
    fun hello() = "Hello, World!"
}