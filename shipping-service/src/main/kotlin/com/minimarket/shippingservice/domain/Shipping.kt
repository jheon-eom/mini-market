package com.minimarket.shippingservice.domain

class Shipping(
    val id: ShippingId? = null,

    val orderId: String,

    val receiverName: String,

    var address: String,

    var detailAddress: String,

    var status: ShippingStatus,
)