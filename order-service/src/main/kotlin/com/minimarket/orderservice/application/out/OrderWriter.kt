package com.minimarket.orderservice.application.out

import com.minimarket.orderservice.domain.Order

interface OrderWriter {
    fun save(order: Order): Order

    fun update(order: Order)
}