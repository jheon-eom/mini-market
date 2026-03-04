package com.minimarket.shippingservice.adapter.out.persistence.jpa

import com.minimarket.shippingservice.adapter.out.persistence.jpa.entity.ShippingEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ShippingRepository: JpaRepository<ShippingEntity, Long> {
}