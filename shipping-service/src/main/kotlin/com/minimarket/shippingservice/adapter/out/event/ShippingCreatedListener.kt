package com.minimarket.shippingservice.adapter.out.event

import com.minimart.common.event.kafka.EventPublisher
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.ShippingCreated
import org.slf4j.LoggerFactory
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ShippingCreatedListener(
    private val eventPublisher: EventPublisher,
) {
    private val logger = LoggerFactory.getLogger(ShippingCreatedListener::class.java)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000L, multiplier = 2.0)
    )
    fun handleShippingCreatedEvent(event: ShippingCreated) {
        logger.info("[Shipping] Handling ShippingCreatedEvent for orderId: ${event.orderId}, shippingId: ${event.shippingId}")

        eventPublisher.publish(
            topic = EventTopic.SHIPPING_CREATED,
            partitionKey = event.orderId,
            event = ShippingCreated(
                eventId = event.eventId,
                orderId = event.orderId,
                shippingId = event.shippingId
            )
        )

        logger.info("[Shipping] ShippingCreated event published to Kafka for orderId: ${event.orderId}")
    }
}