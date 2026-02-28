package com.minimarket.paymentservice.application

import com.minimarket.paymentservice.application.dto.PaymentCreateCommand
import com.minimarket.paymentservice.application.`in`.PaymentCreateUseCase
import com.minimarket.paymentservice.application.out.EventOutBoxFinder
import com.minimarket.paymentservice.application.out.PaymentWriter
import com.minimarket.paymentservice.domain.Payment
import com.minimarket.paymentservice.domain.PaymentStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentCreateService(
    private val eventOutBoxFinder: EventOutBoxFinder,
    private val paymentWriter: PaymentWriter
): PaymentCreateUseCase{
    override fun createPay(command: PaymentCreateCommand) {
        // event 멱등성 검사
        if (eventOutBoxFinder.existsByEventId(command.eventId)) {
            return
        }

        // 결제 생성 로직
        paymentWriter.save(
            Payment(
                orderId = command.orderId,
                status = PaymentStatus.PENDING,
                buyerId = command.buyerId,
                orderAmount = command.orderAmount
            )
        )
    }
}