package com.minimarket.paymentservice.application.out

import com.minimarket.paymentservice.domain.Payment

interface PaymentWriter {
    fun save(payment: Payment): Payment

    fun update(payment: Payment): Payment
}