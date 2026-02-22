package com.minimarket.orderservice.adapter.out.persistence.jpa

import com.minimarket.orderservice.adapter.out.persistence.jpa.entity.OrderLineEntity
import org.springframework.data.jpa.repository.JpaRepository

interface OrderLineRepository: JpaRepository<OrderLineEntity, Long> {
}