package com.minimarket.paymentservice.application.dto

import java.math.BigDecimal

data class PaymentCreateCommand(
    val eventId: String,
    val orderId: String,
    val buyerId: Long,
    val orderAmount: BigDecimal
)

data class PaymentProcessCommand(
    val orderId: String,
    val orderAmount: BigDecimal,
    val txId: String
)

data class PaymentProcessResult(
    val txId: String,
    val status: String
)