package com.minimarket.orderservice.domain

import java.math.BigDecimal
import java.time.LocalDateTime

class Order(
    val id: OrderId? = null,

    val buyerId: BuyerId,

    val lines: List<OrderLine>,

    val amount: BigDecimal,

    val status: OrderStatus,

    val orderedAt: LocalDateTime,
)
