package com.minimarket.orderservice.adapter.out.persistence.jpa

import com.minimarket.orderservice.adapter.out.persistence.jpa.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OrderRepository: JpaRepository<OrderEntity, UUID> {
}