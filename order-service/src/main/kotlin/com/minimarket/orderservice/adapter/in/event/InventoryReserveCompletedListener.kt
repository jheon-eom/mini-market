package com.minimarket.orderservice.adapter.`in`.event

import com.minimarket.orderservice.application.`in`.OrderStatusUpdateUseCase
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.InventoryReserved
import com.minimart.common.exception.OutBoxWriteException
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class InventoryReserveCompletedListener(
    private val objectMapper: ObjectMapper,
    private val orderStatusUpdateUseCase: OrderStatusUpdateUseCase
) {
    @KafkaListener(
        topics = [EventTopic.INVENTORY_RESERVED],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000L, multiplier = 2.0)
    )
    fun inventoryReservedListener(message: String, acknowledgment: Acknowledgment) {
        val command = objectMapper.readValue(message, InventoryReserved::class.java)
            .let {
                OrderReserveSuccessCommand(
                    eventId = it.eventId,
                    eventType = it.eventType,
                    orderId = it.orderId
                )
            }

        orderStatusUpdateUseCase.updateToReserved(command)
    }

    @KafkaListener(
        topics = [EventTopic.INVENTORY_FAILED],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000L, multiplier = 2.0)
    )
    fun inventoryReserveFailListener(message: String, acknowledgment: Acknowledgment) {
        val command = objectMapper.readValue(message, InventoryReserved::class.java)
            .let {
                OrderReserveFailedCommand(
                    eventId = it.eventId,
                    eventType = it.eventType,
                    orderId = it.orderId
                )
            }

        orderStatusUpdateUseCase.updateToReserveFail(command)
    }
}