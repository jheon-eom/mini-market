package com.minimarket.orderservice.domain

import java.math.BigDecimal

class OrderLine(
    val orderId: String? = null,

    val productId: Long,

    val quantity: Int,

    val price: BigDecimal,

    val amount: BigDecimal,
) {
    fun totalAmount(): BigDecimal {
        return amount.multiply(BigDecimal(quantity))
    }
}