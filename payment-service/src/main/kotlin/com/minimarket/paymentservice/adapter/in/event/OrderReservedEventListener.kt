package com.minimarket.paymentservice.adapter.`in`.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.minimarket.paymentservice.application.dto.PaymentCreateCommand
import com.minimarket.paymentservice.application.`in`.PaymentCreateUseCase
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.OrderReserved
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import kotlin.jvm.java

@Component
class OrderReservedEventListener(
    private val objectMapper: ObjectMapper,
    private val paymentCreateUseCase: PaymentCreateUseCase
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    @KafkaListener(
        topics = [EventTopic.ORDER_RESERVED],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 500L)
    )
    fun createPay(event: String) {
        val orderReservedEvent = objectMapper.readValue(event, OrderReserved::class.java)
        logger.info("[Payment] ORDER_RESERVED 이벤트 수신 - orderId: ${orderReservedEvent.orderId}, buyerId: ${orderReservedEvent.buyerId}, traceId: ${orderReservedEvent.traceId}")

        paymentCreateUseCase.createPay(
            PaymentCreateCommand(
                eventId = orderReservedEvent.eventId,
                orderId = orderReservedEvent.orderId,
                buyerId = orderReservedEvent.buyerId,
                orderAmount = orderReservedEvent.orderAmount
            )
        )

        logger.info("[Payment] 결제 정보 생성 요청 완료 - orderId: ${orderReservedEvent.orderId}")
    }
}