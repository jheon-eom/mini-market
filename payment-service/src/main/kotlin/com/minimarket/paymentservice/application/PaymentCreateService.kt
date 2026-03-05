package com.minimarket.paymentservice.application

import com.minimarket.paymentservice.application.dto.PaymentCreateCommand
import com.minimarket.paymentservice.application.`in`.PaymentCreateUseCase
import com.minimarket.paymentservice.application.out.EventOutBoxFinder
import com.minimarket.paymentservice.application.out.PaymentWriter
import com.minimarket.paymentservice.domain.Payment
import com.minimarket.paymentservice.domain.PaymentStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentCreateService(
    private val eventOutBoxFinder: EventOutBoxFinder,
    private val paymentWriter: PaymentWriter
): PaymentCreateUseCase{
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun createPay(command: PaymentCreateCommand) {
        logger.info("[Payment] 결제 정보 생성 시작 - orderId: ${command.orderId}, buyerId: ${command.buyerId}, amount: ${command.orderAmount}")

        // event 멱등성 검사
        if (eventOutBoxFinder.existsByEventId(command.eventId)) {
            logger.info("[Payment] 중복 이벤트 무시 - eventId: ${command.eventId}")
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

        logger.info("[Payment] 결제 정보 생성 완료 - orderId: ${command.orderId}, status: PENDING")
    }
}