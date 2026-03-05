package com.minimart.common.tracing

import io.micrometer.tracing.Tracer
import org.springframework.stereotype.Component

/**
 * 현재 Thread의 Trace Context에서 traceId와 spanId를 추출하는 유틸리티
 */
@Component
class TraceContextHolder(
    private val tracer: Tracer
) {
    /**
     * 현재 요청의 traceId를 반환
     * traceId는 전체 분산 트랜잭션을 추적하는 고유 ID
     */
    fun getCurrentTraceId(): String? {
        return tracer.currentSpan()?.context()?.traceId()
    }

    /**
     * 현재 span의 spanId를 반환
     * spanId는 특정 작업(서비스 호출, DB 쿼리 등)을 추적하는 ID
     */
    fun getCurrentSpanId(): String? {
        return tracer.currentSpan()?.context()?.spanId()
    }

    /**
     * 현재 추적 정보가 있는지 확인
     */
    fun hasActiveTrace(): Boolean {
        return tracer.currentSpan() != null
    }

    /**
     * 추적 정보를 Map으로 반환 (로깅/이벤트용)
     */
    fun getTraceInfo(): Map<String, String?> {
        return mapOf(
            "traceId" to getCurrentTraceId(),
            "spanId" to getCurrentSpanId()
        )
    }
}