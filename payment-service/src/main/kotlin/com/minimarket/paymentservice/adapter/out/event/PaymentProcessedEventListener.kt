package com.minimarket.paymentservice.adapter.out.event

import com.minimart.common.event.kafka.EventPublisher
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.PaymentFailed
import com.minimart.common.event.kafka.PaymentProcessed
import org.slf4j.LoggerFactory
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PaymentProcessedEventListener(
    private val eventPublisher: EventPublisher,
) {
    private val logger = LoggerFactory.getLogger(PaymentProcessedEventListener::class.java)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000L, multiplier = 2.0)
    )
    fun handlePaymentProcessedEvent(event: PaymentProcessed) {
        logger.info("[Payment] Handling PaymentProcessedEvent for orderId: ${event.orderId}, paymentId: ${event.paymentId}")

        // 결제 처리 완료 이벤트를 Kafka로 발행
        eventPublisher.publish(
            topic = EventTopic.PAYMENT_PROCESSED,
            partitionKey = event.orderId,
            event = PaymentProcessed(
                eventId = event.eventId,
                orderId = event.orderId,
                paymentId = event.paymentId
            )
        )

        logger.info("[Payment] PaymentProcessed event published to Kafka for orderId: ${event.orderId}")
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000L, multiplier = 2.0)
    )
    fun handlePaymentFailedEvent(event: PaymentFailed) {
        logger.info("[Payment] Handling PaymentFailedEvent for orderId: ${event.orderId}")

        // 결제 처리 완료 이벤트를 Kafka로 발행
        eventPublisher.publish(
            topic = EventTopic.PAYMENT_FAILED,
            partitionKey = event.orderId,
            event = PaymentFailed(
                eventId = event.eventId,
                orderId = event.orderId,
            )
        )

        logger.info("[Payment] PaymentFailed event published to Kafka for orderId: ${event.orderId}")
    }
}