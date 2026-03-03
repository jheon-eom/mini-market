package com.minimarket.orderservice.adapter.out.persistence.jpa

import com.minimarket.orderservice.adapter.out.persistence.jpa.entity.OrderLineEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OrderLineRepository: JpaRepository<OrderLineEntity, Long> {
    fun findByOrderId(orderId: String): List<OrderLineEntity>
}