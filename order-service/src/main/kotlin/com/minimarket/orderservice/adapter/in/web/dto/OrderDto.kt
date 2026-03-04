package com.minimarket.orderservice.adapter.`in`.web.dto

import com.minimarket.orderservice.application.dto.OrderCreateCommand
import com.minimarket.orderservice.application.dto.OrderLineCreateCommand
import com.minimarket.orderservice.application.dto.ShippingInfoCreateCommand
import com.minimarket.orderservice.domain.ShippingInfo
import java.math.BigDecimal

data class OrderCreateRequest(
    val buyerId: Long,

    val amount: BigDecimal,

    val orderLines: List<OrderLineCreateRequest>,

    val shippingInfo: ShippingInfoRequest,
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
            },
            shippingInfo = ShippingInfoCreateCommand(
                receiverName = shippingInfo.receiverName,
                address = shippingInfo.address,
                detailAddress = shippingInfo.detailAddress,
            )
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

data class ShippingInfoRequest(
    val receiverName: String,

    val address: String,

    val detailAddress: String,
)