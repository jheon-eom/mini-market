package com.minimarket.accountservice.adapter.`in`.web

import com.minimarket.accountservice.adapter.`in`.web.dto.JoinRequest
import com.minimarket.accountservice.adapter.`in`.web.dto.JoinResponse
import com.minimarket.accountservice.application.port.`in`.JoinUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/accounts")
class AccountController(
    private val joinUseCase: JoinUseCase,
) {
    @PostMapping
    fun join(request: JoinRequest): JoinResponse {
        val result = joinUseCase.join(request.toCommand())

        return JoinResponse(
            id = result.id,
            accessToken = result.accessToken,
            refreshToken = result.refreshToken
        )
    }
}