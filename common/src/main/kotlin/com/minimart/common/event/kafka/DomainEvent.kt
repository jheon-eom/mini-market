package com.minimart.common.event.kafka

import java.time.LocalDateTime

/**
 * 모든 도메인 이벤트의 기본 인터페이스
 */
interface DomainEvent {
    val eventId: String
    val occurredAt: LocalDateTime
    val eventType: String
    val traceId: String?  // 분산 추적을 위한 Trace ID
}