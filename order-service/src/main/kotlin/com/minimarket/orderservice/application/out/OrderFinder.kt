package com.minimarket.orderservice.application.out

import com.minimarket.orderservice.domain.Order

interface OrderFinder {
    fun findById(id: String): Order

    fun findByIdWithOrderLines(id: String): Order
}