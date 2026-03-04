package com.minimarket.shippingservice.adapter.out.persistence.jpa

import com.minimarket.shippingservice.adapter.out.persistence.jpa.entity.ShippingEntity
import com.minimarket.shippingservice.application.out.ShippingWriter
import com.minimarket.shippingservice.domain.Shipping
import org.springframework.stereotype.Repository

@Repository
class ShippingJpaWriter(
    private val repository: ShippingRepository
): ShippingWriter {
    override fun save(shipping: Shipping): Shipping =
        ShippingEntity.from(shipping)
            .let { repository.save(it) }
            .toDomain()
}