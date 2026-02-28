package com.minimarket.paymentservice.application.`in`

import com.minimarket.paymentservice.application.dto.PaymentProcessCommand
import com.minimarket.paymentservice.application.dto.PaymentProcessResult

interface PaymentProcessUseCase {
    fun process(command: PaymentProcessCommand): PaymentProcessResult
}