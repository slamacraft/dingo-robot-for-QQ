package com.dingo.config.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
open class ExceptionHandler {

    @ExceptionHandler(Exception::class)
    open fun defaultHandler(exception: Exception): ResponseEntity<Map<String, String>> {
        exception.printStackTrace()
        return ResponseEntity<Map<String, String>>(
            mutableMapOf(
                "code" to "500",
                "msg" to exception.message!!
            ), HttpStatus.BAD_REQUEST
        )
    }

}
