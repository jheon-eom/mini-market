package com.minimarket.orderservice.domain

import java.math.BigDecimal
import java.time.LocalDateTime

class Order(
    val id: OrderId? = null,

    val buyerId: BuyerId,

    val lines: List<OrderLine>,

    val amount: BigDecimal,

    var status: OrderStatus,

    val orderedAt: LocalDateTime,
) {
    fun reserve() {
        status = OrderStatus.RESERVED
    }

    fun reserveFail() {
        status = OrderStatus.RESERVE_FAILED
    }

    fun pay() {
        status = OrderStatus.PAID
    }

    fun payFail() {
        status = OrderStatus.CANCELLED
    }
}
