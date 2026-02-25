package com.minimarket.catalogtservice.adapter.out.event

import com.minimarket.catalogtservice.domain.event.InventoryReserveCompletedEvent
import com.minimart.common.event.EventPublisher
import com.minimart.common.event.EventTopic
import com.minimart.common.event.InventoryFailed
import com.minimart.common.event.InventoryReserved
import org.slf4j.LoggerFactory
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

/**
 * 트랜잭션 커밋 후 재고 예약 이벤트를 Kafka로 발행하는 리스너
 *
 * Kafka 발행 실패 시 최대 3회까지 재시도 (1초, 2초, 4초 간격)
 * 재시도에도 실패하면 로그만 남기고, 아웃박스에 저장된 이벤트는 별도 스케줄러로 재발행 가능
 */
@Component
class InventoryEventPublisher(
    private val eventPublisher: EventPublisher
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(
        maxAttempts = 3,
        backoff = Backoff(delay = 1000, multiplier = 2.0)
    )
    fun handleInventoryReserveCompleted(event: InventoryReserveCompletedEvent) {
        logger.info("Publishing inventory event to Kafka - eventType: ${event.eventType}, orderId: ${event.orderId}")

        try {
            when (event.eventType) {
                EventTopic.INVENTORY_RESERVED -> {
                    eventPublisher.publish(
                        topic = EventTopic.INVENTORY_RESERVED,
                        partitionKey = event.orderId,
                        event = InventoryReserved(
                            eventId = event.eventId,
                            orderId = event.orderId
                        )
                    )
                }
                EventTopic.INVENTORY_FAILED -> {
                    eventPublisher.publish(
                        topic = EventTopic.INVENTORY_FAILED,
                        partitionKey = event.orderId,
                        event = InventoryFailed(
                            eventId = event.eventId,
                            orderId = event.orderId
                        )
                    )
                }
                else -> {
                    logger.warn("Unknown event type: ${event.eventType}")
                }
            }
            logger.info("Successfully published inventory event - eventType: ${event.eventType}, orderId: ${event.orderId}")
        } catch (e: Exception) {
            logger.error("Failed to publish inventory event - eventType: ${event.eventType}, orderId: ${event.orderId}", e)
            throw e
        }
    }
}