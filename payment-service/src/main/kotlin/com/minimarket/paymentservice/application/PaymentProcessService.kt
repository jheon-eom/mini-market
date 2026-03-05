package com.minimarket.paymentservice.application

import com.minimarket.paymentservice.application.dto.PaymentProcessCommand
import com.minimarket.paymentservice.application.dto.PaymentProcessResult
import com.minimarket.paymentservice.application.`in`.PaymentProcessUseCase
import com.minimarket.paymentservice.application.out.EventOutBoxWriter
import com.minimarket.paymentservice.application.out.PaymentFinder
import com.minimarket.paymentservice.application.out.PaymentWriter
import com.minimarket.paymentservice.domain.PaymentApiException
import com.minimarket.paymentservice.domain.PaymentErrorCode
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.PaymentFailed
import com.minimart.common.event.kafka.PaymentProcessed
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * 외부 시스템과의 통신, 트랜잭션 관리, 이벤트 발행 등을 담당하는 서비스
 */
@Service
class PaymentProcessService(
    private val paymentFinder: PaymentFinder,
    private val paymentWriter: PaymentWriter,
    private val eventOutBoxWriter: EventOutBoxWriter,
    private val applicationEventPublisher: ApplicationEventPublisher,
): PaymentProcessUseCase {
    private val logger = LoggerFactory.getLogger(PaymentProcessService::class.java)

    @Transactional
    override fun process(command: PaymentProcessCommand): PaymentProcessResult {
        // 결제 금액 검증
        val payment = paymentFinder.findByOrderId(command.orderId)

        try {
            if (payment == null) {
                logger.warn("[Payment] 결제 정보 없음: orderId = ${command.orderId}")
                throw PaymentApiException(PaymentErrorCode.NOT_FOUND)
            }

            payment.process(command.orderAmount, command.txId)
            paymentWriter.update(payment)
        } catch (e: PaymentApiException) {
            if (e.code == PaymentErrorCode.INVALID_PAYMENT_AMOUNT.code) {
                logger.warn(
                    "[Payment] 결제 금액 검증 실패:" +
                            " orderId = ${command.orderId}, " +
                            "expected = ${payment!!.orderAmount}, " +
                            "actual = ${command.orderAmount}"
                )
            }

            // 결제 실패 이벤트 발행
            // 이벤트 아웃박스 저장
            val eventId = UUID.randomUUID().toString()
            eventOutBoxWriter.save(
                eventId = eventId,
                eventType = EventTopic.PAYMENT_FAILED,
                relationId = command.orderId
            )

            // 이벤트 발행 (스프링 트랜잭션 리스너)
            val paymentFailedEvent = PaymentFailed(
                eventId = eventId,
                orderId = command.orderId,
            )
            applicationEventPublisher.publishEvent(paymentFailedEvent)

            return PaymentProcessResult(
                txId = command.txId,
                status = "FAILED"
            )
        }

        // 이벤트 아웃박스 저장
        val eventId = UUID.randomUUID().toString()
        eventOutBoxWriter.save(
            eventId = eventId,
            eventType = EventTopic.PAYMENT_PROCESSED,
            relationId = command.orderId
        )

        // 이벤트 발행 (스프링 트랜잭션 리스너)
        val paymentProcessedEvent = PaymentProcessed(
            eventId = eventId,
            orderId = command.orderId,
            paymentId = payment.paymentId!!.value.toString()
        )
        applicationEventPublisher.publishEvent(paymentProcessedEvent)

        logger.info("[Payment] 결제 처리 완료: orderId = ${command.orderId}, paymentId = ${payment.paymentId!!.value}")

        return PaymentProcessResult(
            txId = command.txId,
            status = payment.status.name
        )
    }
}