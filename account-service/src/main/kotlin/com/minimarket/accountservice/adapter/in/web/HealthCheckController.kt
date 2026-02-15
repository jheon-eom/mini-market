package com.minimarket.accountservice.adapter.`in`.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/health")
class HealthCheckController {
    @GetMapping
    fun healthCheck(): String {
        return "Account Service is up and running!"
    }
}