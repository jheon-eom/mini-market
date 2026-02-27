package com.minimarket.orderservice.adapter.out.persistence.jpa

import com.minimarket.orderservice.adapter.out.persistence.jpa.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface OrderRepository: JpaRepository<OrderEntity, UUID> {
    @Modifying
    @Query("UPDATE OrderEntity o SET o.status = 'RESERVED' WHERE o.id = :id")
    fun updateById(value: String)
}