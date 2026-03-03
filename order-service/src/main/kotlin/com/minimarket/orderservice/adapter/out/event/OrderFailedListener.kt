package com.minimarket.orderservice.adapter.out.event

import com.minimart.common.event.kafka.EventPublisher
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.OrderFailed
import com.minimart.common.event.kafka.OrderLine
import org.slf4j.LoggerFactory
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class OrderFailedListener(
    private val eventPublisher: EventPublisher,
) {
    private val logger = LoggerFactory.getLogger(OrderFailedListener::class.java)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000L, multiplier = 2.0)
    )
    fun handleOrderFailed(event: OrderFailed) {
        logger.info("Handling OrderFailedEvent for orderId: ${event.orderId}")

        eventPublisher.publish(
            topic = EventTopic.ORDER_RESERVED,
            partitionKey = event.orderId,
            event = OrderFailed(
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