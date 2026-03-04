package com.minimarket.orderservice.application.dto

import com.minimarket.orderservice.domain.OrderId
import java.math.BigDecimal

data class OrderCreateCommand(
    val buyerId: Long,

    val amount: BigDecimal,

    val orderLines: List<OrderLineCreateCommand>,

    val shippingInfo: ShippingInfoCreateCommand
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

data class OrderPaymentSuccessCommand(
    val orderId: OrderId,

    val eventId: String
)

data class OrderPaymentFailedCommand(
    val orderId: OrderId,

    val eventId: String
)

data class ShippingInfoCreateCommand(
    val receiverName: String,

    val address: String,

    val detailAddress: String,
)