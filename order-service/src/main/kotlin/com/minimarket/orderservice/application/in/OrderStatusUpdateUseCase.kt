package com.minimarket.orderservice.application.`in`

import com.minimarket.orderservice.adapter.`in`.event.OrderReserveSuccessCommand

interface OrderStatusUpdateUseCase {
    fun updateToReserved(command: OrderReserveSuccessCommand)
}