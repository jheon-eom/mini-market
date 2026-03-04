package com.minimarket.orderservice.application.`in`

import com.minimarket.orderservice.application.dto.OrderCreateCommand
import com.minimarket.orderservice.application.dto.OrderCreateResult

interface OrderUseCase {
    fun create(command: OrderCreateCommand): OrderCreateResult
}