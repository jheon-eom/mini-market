package com.minimarket.orderservice.adapter.out.persistence.jpa

import com.minimarket.orderservice.adapter.out.persistence.jpa.entity.ShippingInfoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ShippingInfoRepository: JpaRepository<ShippingInfoEntity, Long> {
    fun findByOrderId(toString: String): ShippingInfoEntity?
}