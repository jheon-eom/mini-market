package com.minimarket.orderservice.application.dto

import java.math.BigDecimal

data class OrderCreateCommand(
    val buyerId: Long,

    val amount: BigDecimal,

    val orderLines: List<OrderLineCreateCommand>,
)

data class OrderLineCreateCommand(
    val productId: Long,

    val quantity: Int,

    val price: BigDecimal,

    val amount: BigDecimal,
)

data class OrderCreateResult(
    val orderId: String,
)