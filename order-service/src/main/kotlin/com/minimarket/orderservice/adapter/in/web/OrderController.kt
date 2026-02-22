package com.minimarket.orderservice.adapter.`in`.web

import com.minimarket.orderservice.adapter.`in`.web.dto.OrderCreateRequest
import com.minimarket.orderservice.adapter.`in`.web.dto.OrderCreatedResponse
import com.minimarket.orderservice.application.`in`.OrderUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val orderUseCase: OrderUseCase
) {
    @PostMapping
    fun create(request: OrderCreateRequest): ResponseEntity<OrderCreatedResponse> {
        val result = orderUseCase.create(request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(OrderCreatedResponse(orderId = result.orderId))
    }
}