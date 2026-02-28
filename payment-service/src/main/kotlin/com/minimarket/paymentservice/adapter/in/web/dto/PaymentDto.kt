package com.minimarket.paymentservice.adapter.`in`.web.dto

import com.minimarket.paymentservice.application.dto.PaymentProcessCommand
import java.math.BigDecimal

data class PaymentProcessRequest(
    val orderId: String,
    val amount: BigDecimal,
    val txId: String,
) {
    fun toCommand(): PaymentProcessCommand {
        return PaymentProcessCommand(
            orderId = orderId,
            orderAmount = amount,
            txId = txId
        )
    }
}