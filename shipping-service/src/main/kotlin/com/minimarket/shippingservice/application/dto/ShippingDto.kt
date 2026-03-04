package com.minimarket.shippingservice.application.dto

data class ShippingCreateCommand(
    val eventId: String,

    val orderId: String,

    val receiverName : String,

    val address: String,

    val detailAddress: String,
)