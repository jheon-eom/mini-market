package com.minimarket.orderservice.adapter.`in`.web.dto

import com.minimarket.orderservice.application.dto.OrderCreateCommand
import com.minimarket.orderservice.application.dto.OrderLineCreateCommand
import java.math.BigDecimal

data class OrderCreateRequest(
    val buyerId: Long,

    val amount: BigDecimal,

    val orderLines: List<OrderLineCreateRequest>,
) {
    fun toCommand(): OrderCreateCommand {
        return OrderCreateCommand(
            buyerId = buyerId,
            amount = amount,
            orderLines = orderLines.map {
                OrderLineCreateCommand(
                    productId = it.productId,
                    quantity = it.quantity,
                    price = it.price,
                    amount = it.amount,
                )
            }
        )
    }
}

data class OrderLineCreateRequest(
    val productId: Long,

    val quantity: Int,

    val price: BigDecimal,

    val amount: BigDecimal,
)

data class OrderCreatedResponse(
    val orderId: String,
)