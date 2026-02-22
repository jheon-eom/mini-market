package com.minimarket.orderservice.adapter.out.persistence.jpa.entity

import com.minimarket.orderservice.domain.OrderLine
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "order_line")
class OrderLineEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, updatable = false)
    val orderId: String,

    @Column(nullable = false, updatable = false)
    val productId: Long,

    @Column(nullable = false)
    var quantity: Int,

    @Column(nullable = false, updatable = false)
    val price: BigDecimal,

    @Column(nullable = false)
    var amount: BigDecimal,
): BaseEntity() {
    fun toDomain(): OrderLine {
        return OrderLine(
            orderId = orderId,
            productId = productId,
            quantity = quantity,
            price = price,
            amount = amount,
        )
    }
}