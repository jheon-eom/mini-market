package com.minimarket.paymentservice.adapter.`in`.web

import com.minimarket.paymentservice.adapter.`in`.web.dto.PaymentProcessRequest
import com.minimarket.paymentservice.application.`in`.PaymentProcessUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(
    private val paymentProcessUseCase: PaymentProcessUseCase
) {
    @PostMapping("/process")
    fun process(request: PaymentProcessRequest): ResponseEntity<Any> {
        paymentProcessUseCase.process(request.toCommand())
        return ResponseEntity.ok().build()
    }
}