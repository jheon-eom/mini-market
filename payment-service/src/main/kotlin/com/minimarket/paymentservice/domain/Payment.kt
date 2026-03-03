package com.minimarket.paymentservice.domain

import java.math.BigDecimal

class Payment(
    val paymentId: PaymentId? = null,

    val orderId: String,

    val status: PaymentStatus,

    val buyerId: Long,

    val orderAmount: BigDecimal,

    var txId: String? = null,
) {
    fun process(orderAmount: BigDecimal, txId: String) {
        if (this.orderAmount.compareTo(orderAmount) != 0) {
            throw PaymentApiException(PaymentErrorCode.INVALID_PAYMENT_AMOUNT)
        }

        this.txId = txId
    }
}