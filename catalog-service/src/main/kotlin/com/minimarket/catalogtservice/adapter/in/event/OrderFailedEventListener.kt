package com.minimarket.catalogtservice.adapter.`in`.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.minimarket.catalogtservice.application.dto.InventoryRollbackCommand
import com.minimarket.catalogtservice.application.`in`.InventoryRollbackUseCase
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.OrderFailed
import com.minimart.common.event.kafka.OrderLine
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @KafkaListener(
        topics = [EventTopic.ORDER_FAILED],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(
        value = [RuntimeException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000L, multiplier = 2.0)
    )
    fun handleOrderFailed(event: String) {
        logger.info("Order failed: {}", event)

        val event = objectMapper.readValue(event, OrderFailed::class.java)

        inventoryRollbackUseCase.rollback(
            InventoryRollbackCommand(
                eventId = event.eventId,
                orderId = event.orderId,
                orderLines = event.orderLines.map {
                    OrderLine(
                        productId = it.productId,
                        quantity = it.quantity,
                        price = it.price,
                        amount = it.amount
                    )
                }
            )
        )
    }
}