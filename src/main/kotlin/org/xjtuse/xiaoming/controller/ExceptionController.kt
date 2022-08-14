package org.xjtuse.xiaoming.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ExceptionController : ResponseEntityExceptionHandler() {
    @ExceptionHandler
    fun userNotFoundException(e: UsernameNotFoundException, request: WebRequest) =
        handleExceptionInternal(e, e.message, HttpHeaders(), HttpStatus.NOT_FOUND, request)
}