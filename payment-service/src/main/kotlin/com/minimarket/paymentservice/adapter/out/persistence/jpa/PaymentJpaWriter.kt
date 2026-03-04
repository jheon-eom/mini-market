package com.minimarket.paymentservice.adapter.out.persistence.jpa

import com.minimarket.paymentservice.adapter.out.persistence.jpa.entity.PaymentEntity
import com.minimarket.paymentservice.application.out.PaymentWriter
import com.minimarket.paymentservice.domain.Payment
import org.springframework.stereotype.Component

@Component
class PaymentJpaWriter(
    private val paymentRepository: PaymentRepository
): PaymentWriter {
    override fun save(payment: Payment): Payment {
        val paymentEntity = PaymentEntity.from(payment)
        return paymentRepository.save(paymentEntity).toDomain()
    }

    override fun update(payment: Payment): Payment {
        val paymentEntity = paymentRepository.findByOrderId(payment.orderId)
            ?: throw IllegalArgumentException("Payment with orderId ${payment.orderId} not found")

        paymentEntity.txId = payment.txId
        paymentEntity.status = payment.status

        return paymentRepository.save(paymentEntity).toDomain()
    }
}