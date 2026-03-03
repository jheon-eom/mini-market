package com.minimarket.catalogtservice.adapter.out.event

import com.minimart.common.event.application.InventoryReserveCompleteEvent
import com.minimart.common.event.kafka.EventPublisher
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.InventoryFailed
import com.minimart.common.event.kafka.InventoryReserved
import org.slf4j.LoggerFactory
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class InventoryReserveEventListener(
    private val eventPublisher: EventPublisher
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000L, multiplier = 2.0)
    )
    fun handleInventoryReservedEvent(event: InventoryReserved) {
        logger.info("Publishing InventoryReserved event for orderId: ${event.orderId}")

        eventPublisher.publish(
            topic = EventTopic.INVENTORY_RESERVED,
            partitionKey = event.orderId,
            event = InventoryReserved(
                orderId = event.orderId,
            )
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000L, multiplier = 2.0)
    )
    fun handleInventoryReserveFailedEvent(event: InventoryFailed) {
        logger.info("Publishing InventoryFailed event for orderId: ${event.orderId}")

        eventPublisher.publish(
            topic = EventTopic.INVENTORY_FAILED,
            partitionKey = event.orderId,
            event = InventoryReserved(
                orderId = event.orderId,
            )
        )
    }
}