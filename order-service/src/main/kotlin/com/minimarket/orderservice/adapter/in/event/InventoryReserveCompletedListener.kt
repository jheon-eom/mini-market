package com.minimarket.orderservice.adapter.`in`.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.minimarket.orderservice.application.`in`.OrderStatusUpdateUseCase
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.InventoryFailed
import com.minimart.common.event.kafka.InventoryReserved
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import kotlin.jvm.java

@Component
class InventoryReserveCompletedListener(
    private val objectMapper: ObjectMapper,
    private val orderStatusUpdateUseCase: OrderStatusUpdateUseCase
) {
    private val logger = LoggerFactory.getLogger(javaClass)
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
        val event = objectMapper.readValue(message, InventoryReserved::class.java)
        logger.info("[Order] INVENTORY_RESERVED 이벤트 수신 - orderId: ${event.orderId}, traceId: ${event.traceId}")

        val command = OrderReserveSuccessCommand(
            eventId = event.eventId,
            eventType = event.eventType,
            orderId = event.orderId
        )

        orderStatusUpdateUseCase.updateToReserved(command)

        logger.info("[Order] 재고 예약 성공 처리 완료 - orderId: ${event.orderId}")
        acknowledgment.acknowledge()
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
        val event = objectMapper.readValue(message, InventoryFailed::class.java)
        logger.info("[Order] INVENTORY_FAILED 이벤트 수신 - orderId: ${event.orderId}, traceId: ${event.traceId}")

        val command = OrderReserveFailedCommand(
            eventId = event.eventId,
            eventType = event.eventType,
            orderId = event.orderId
        )

        orderStatusUpdateUseCase.updateToReserveFail(command)

        logger.info("[Order] 재고 예약 실패 처리 완료 - orderId: ${event.orderId}")
        acknowledgment.acknowledge()
    }
}