package com.minimarket.orderservice.adapter.`in`.event

data class OrderReserveSuccessCommand(
    val eventId: String,

    val eventType: String,

    val orderId: String,
)