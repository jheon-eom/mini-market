package com.minimarket.paymentservice.adapter.out.persistence.jpa

import com.minimarket.paymentservice.adapter.out.persistence.jpa.entity.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository: JpaRepository<PaymentEntity, Long> {
    fun findByOrderId(orderId: String): PaymentEntity?
}