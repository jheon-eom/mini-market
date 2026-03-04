package com.minimarket.shippingservice.adapter.out.persistence.jpa.entity

import com.minimarket.shippingservice.domain.Shipping
import com.minimarket.shippingservice.domain.ShippingId
import com.minimarket.shippingservice.domain.ShippingStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "shipping")
class ShippingEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    val orderId: String,

    val receiverName: String,

    var address: String,

    var detailAddress: String,

    @Enumerated(EnumType.STRING)
    var status: ShippingStatus
): BaseEntity() {
    fun toDomain(): Shipping {
        return Shipping(
            id = ShippingId(this.id!!),
            orderId = this.orderId,
            receiverName = this.receiverName,
            address = this.address,
            detailAddress = this.detailAddress,
            status = this.status
        )
    }

    companion object {
        fun from(shipping: Shipping): ShippingEntity {
            return ShippingEntity(
                orderId = shipping.orderId,
                receiverName = shipping.receiverName,
                address = shipping.address,
                detailAddress = shipping.detailAddress,
                status = shipping.status
            )
        }
    }
}