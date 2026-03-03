package com.minimarket.orderservice.adapter.out.event

import com.minimart.common.event.kafka.EventPublisher
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.OrderReserved
import org.slf4j.LoggerFactory
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class OrderReservedListener(
    private val eventPublisher: EventPublisher,
) {
    private val logger = LoggerFactory.getLogger(OrderReservedListener::class.java)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000L, multiplier = 2.0)
    )
    fun handleOrderReserved(event: OrderReserved) {
        logger.info("Handling OrderStatusUpdatedEvent for orderId: ${event.orderId}")
        // 주문 상태 업데이트 이벤트를 Kafka로 발행
        eventPublisher.publish(
            topic = EventTopic.ORDER_RESERVED,
            partitionKey = event.orderId,
            event = OrderReserved(
                orderId = event.orderId,
                buyerId = event.buyerId,
                orderAmount = event.orderAmount
            )
        )
    }
}