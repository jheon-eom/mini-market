package com.minimarket.orderservice.adapter.`in`.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.minimarket.orderservice.application.dto.OrderPaymentFailedCommand
import com.minimarket.orderservice.application.dto.OrderPaymentSuccessCommand
import com.minimarket.orderservice.application.`in`.OrderStatusUpdateUseCase
import com.minimarket.orderservice.domain.OrderId
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.PaymentFailed
import com.minimart.common.event.kafka.PaymentProcessed
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import kotlin.jvm.java

@Component
class PaymentProcessedEventListener(
    private val objectMapper: ObjectMapper,
    private val orderStatusUpdateUseCase: OrderStatusUpdateUseCase
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    @KafkaListener(
        topics = [EventTopic.PAYMENT_PROCESSED],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000L, multiplier = 2.0)
    )
    fun paymentProcessedListener(message: String) {
        val event = objectMapper.readValue(message, PaymentProcessed::class.java)
        logger.info("[Order] PAYMENT_PROCESSED 이벤트 수신 - orderId: ${event.orderId}, paymentId: ${event.paymentId}, traceId: ${event.traceId}")

        orderStatusUpdateUseCase.updateToPaymentSuccess(
            OrderPaymentSuccessCommand(
                orderId = OrderId(event.orderId),
                eventId = event.eventId
            )
        )

        logger.info("[Order] 결제 성공 이벤트 처리 완료 - orderId: ${event.orderId}")
    }

    @KafkaListener(
        topics = [EventTopic.PAYMENT_FAILED],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000L, multiplier = 2.0)
    )
    fun paymentFailedListener(message: String) {
        val event = objectMapper.readValue(message, PaymentFailed::class.java)
        logger.info("[Order] PAYMENT_FAILED 이벤트 수신 - orderId: ${event.orderId}, traceId: ${event.traceId}")

        orderStatusUpdateUseCase.updateToPaymentFailed(
            OrderPaymentFailedCommand(
                orderId = OrderId(event.orderId),
                eventId = event.eventId
            )
        )

        logger.info("[Order] 결제 실패 이벤트 처리 완료 - orderId: ${event.orderId}")
    }
}