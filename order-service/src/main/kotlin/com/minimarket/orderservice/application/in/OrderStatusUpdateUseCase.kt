package com.minimarket.orderservice.application.`in`

import com.minimarket.orderservice.adapter.`in`.event.OrderReserveFailedCommand
import com.minimarket.orderservice.adapter.`in`.event.OrderReserveSuccessCommand
import com.minimarket.orderservice.application.dto.OrderPaymentFailedCommand
import com.minimarket.orderservice.application.dto.OrderPaymentSuccessCommand
import com.minimarket.orderservice.domain.OrderId

interface OrderStatusUpdateUseCase {
    fun updateToReserved(command: OrderReserveSuccessCommand)

    fun updateToReserveFail(command: OrderReserveFailedCommand)

    fun updateToPaymentSuccess(command: OrderPaymentSuccessCommand)

    fun updateToPaymentFailed(command: OrderPaymentFailedCommand)
}