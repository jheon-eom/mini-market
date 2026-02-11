package com.minimarket.accountservice.adapter.`in`.web

import com.minimarket.accountservice.adapter.`in`.web.dto.CustomerJoinRequest
import com.minimarket.accountservice.adapter.`in`.web.dto.CustomerJoinResponse
import com.minimarket.accountservice.application.port.`in`.CustomerJoinUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/accounts/customers")
class CustomerAccountController(
    private val customerJoinUseCase: CustomerJoinUseCase,
) {
    @PostMapping
    fun joinCustomer(request: CustomerJoinRequest): CustomerJoinResponse {
        val joinCustomer = customerJoinUseCase.joinCustomer(request.toCommand())

        return CustomerJoinResponse(
            id = joinCustomer.id,
            accessToken = joinCustomer.accessToken,
            refreshToken = joinCustomer.refreshToken
        )
    }
}