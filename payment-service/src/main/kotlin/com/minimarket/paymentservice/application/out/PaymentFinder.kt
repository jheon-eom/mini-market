package com.minimarket.paymentservice.application.out

import com.minimarket.paymentservice.domain.Payment

interface PaymentFinder {
    fun findByOrderId(orderId: String): Payment?
}