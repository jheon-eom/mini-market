package com.minimarket.catalogtservice.adapter.`in`.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.minimarket.catalogtservice.application.dto.InventoryRollbackCommand
import com.minimarket.catalogtservice.application.`in`.InventoryRollbackUseCase
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.PaymentFailed
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import kotlin.jvm.java

@Component
class OrderFailedEventListener(
    private val objectMapper: ObjectMapper,
    private val inventoryRollbackUseCase: InventoryRollbackUseCase
) {
    @KafkaListener(
        topics = [EventTopic.ORDER_FAILED],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000L, multiplier = 2.0)
    )
    fun handlePaymentFailedEvent(event: String) {
        val event = objectMapper.readValue(event, PaymentFailed::class.java)
        inventoryRollbackUseCase.rollback(InventoryRollbackCommand(
            eventId = event.eventId,
            orderId = event.orderId,
        ))
    }
}