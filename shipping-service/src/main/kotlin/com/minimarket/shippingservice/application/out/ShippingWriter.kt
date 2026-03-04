package com.minimarket.shippingservice.application.out

import com.minimarket.shippingservice.domain.Shipping

interface ShippingWriter {
    fun save(shipping: Shipping): Shipping
}