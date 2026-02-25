package com.minimarket.catalogtservice.adapter.`in`.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.minimarket.catalogtservice.application.dto.ProductReserveCommand
import com.minimarket.catalogtservice.application.dto.ProductReserveItem
import com.minimarket.catalogtservice.application.`in`.ProductReserveUseCase
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.OrderCreated
import com.minimart.common.exception.OutBoxWriteException
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class OrderCreatedEventListener(
    private val objectMapper: ObjectMapper,
    private val productReserveUseCase: ProductReserveUseCase,
) {
    private val logger = LoggerFactory.getLogger(OrderCreatedEventListener::class.java)

    @KafkaListener(
        topics = [EventTopic.ORDER_CREATED],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(
        value = [OutBoxWriteException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000L, multiplier = 2.0)
    )
    fun listen(message: String, acknowledgment: Acknowledgment) {
        try {
            logger.info("Received message $message")

            productReserveUseCase.reserve(readEvent(message))

            acknowledgment.acknowledge()
        } catch (e: OutBoxWriteException) {
            logger.error("Failed to write outbox event for message: $message", e)
            throw e
        }
    }

    private fun readEvent(message: String): ProductReserveCommand =
        objectMapper.readValue(message, OrderCreated::class.java)
            .let {
                ProductReserveCommand(
                    eventId = it.eventId,
                    orderId = it.orderId,
                    items = it.orderLines.map { item ->
                        ProductReserveItem(
                            productId = item.productId,
                            quantity = item.quantity
                        )
                    }
                )
            }
}