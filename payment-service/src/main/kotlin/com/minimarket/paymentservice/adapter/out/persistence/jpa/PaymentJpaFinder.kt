package com.minimarket.paymentservice.adapter.out.persistence.jpa

import com.minimarket.paymentservice.application.out.PaymentFinder
import com.minimarket.paymentservice.domain.Payment
import com.minimarket.paymentservice.domain.PaymentApiException
import com.minimarket.paymentservice.domain.PaymentErrorCode
import org.springframework.stereotype.Repository

@Repository
class PaymentJpaFinder(
    private val paymentRepository: PaymentRepository
): PaymentFinder {
    override fun findByOrderId(orderId: String): Payment {
        val paymentEntity = paymentRepository.findByOrderId(orderId)
            ?: throw PaymentApiException(PaymentErrorCode.NOT_FOUND)

        return paymentEntity.toDomain()
    }
}