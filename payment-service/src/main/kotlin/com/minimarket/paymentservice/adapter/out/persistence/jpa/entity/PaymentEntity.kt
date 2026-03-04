package com.minimarket.paymentservice.adapter.out.persistence.jpa.entity

import com.minimarket.paymentservice.domain.Payment
import com.minimarket.paymentservice.domain.PaymentId
import com.minimarket.paymentservice.domain.PaymentStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "payment")
class PaymentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, updatable = false, unique = true)
    val orderId: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: PaymentStatus,

    @Column(nullable = false, updatable = false)
    val buyerId: Long,

    @Column(nullable = false, updatable = false)
    val orderAmount: BigDecimal,

    var txId: String? = null,
): BaseEntity() {
    fun toDomain(): Payment {
        return Payment(
            paymentId = id?.let { PaymentId(it) },
            orderId = orderId,
            status = status,
            buyerId = buyerId,
            orderAmount = orderAmount,
            txId = txId ?: "",
        )
    }

    companion object {
        fun from(payment: Payment): PaymentEntity {
            return PaymentEntity(
                orderId = payment.orderId,
                status = payment.status,
                buyerId = payment.buyerId,
                orderAmount = payment.orderAmount,
            )
        }
    }
}