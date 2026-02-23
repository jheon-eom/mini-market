package com.minimart.common.event

/**
 * 이벤트 발행 인터페이스
 */
interface EventPublisher {
    fun publish(topic: String, partitionKey: String, event: DomainEvent)
}