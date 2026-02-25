package com.minimarket.catalogtservice.domain.event

/**
 * 재고 예약 완료 시 발행되는 Spring 내부 이벤트
 * TransactionalEventListener가 트랜잭션 커밋 후 이 이벤트를 받아서 Kafka로 발행
 */
data class InventoryReserveCompletedEvent(
    val eventId: String,
    val eventType: String,
    val orderId: String
)