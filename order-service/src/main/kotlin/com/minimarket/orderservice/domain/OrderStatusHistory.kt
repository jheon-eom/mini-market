package com.minimarket.orderservice.domain

import java.time.LocalDateTime

class OrderStatusHistory(
    val orderId: OrderId,

    val status: OrderStatus,

    val reason: String,

    val changedAt: LocalDateTime,
) {
}