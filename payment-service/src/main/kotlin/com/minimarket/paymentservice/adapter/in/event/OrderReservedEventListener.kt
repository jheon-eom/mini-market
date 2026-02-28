package com.minimarket.paymentservice.adapter.`in`.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.minimarket.paymentservice.application.dto.PaymentCreateCommand
import com.minimarket.paymentservice.application.`in`.PaymentCreateUseCase
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.OrderReserved
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
        val event = objectMapper.readValue(event, OrderReserved::class.java)
        paymentCreateUseCase.createPay(
            PaymentCreateCommand(
                eventId = event.eventId,
                orderId = event.orderId,
                buyerId = event.buyerId,
                orderAmount = event.orderAmount
            )
        )
    }
}