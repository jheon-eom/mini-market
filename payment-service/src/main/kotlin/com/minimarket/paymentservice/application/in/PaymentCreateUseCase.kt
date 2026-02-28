package com.minimarket.paymentservice.application.`in`

import com.minimarket.paymentservice.application.dto.PaymentCreateCommand

interface PaymentCreateUseCase {
    fun createPay(command: PaymentCreateCommand)
}