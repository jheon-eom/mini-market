package com.minimarket.shippingservice.application.`in`

import com.minimarket.shippingservice.application.dto.ShippingCreateCommand

interface ShippingCreateUseCase {
    fun create(command: ShippingCreateCommand)
}