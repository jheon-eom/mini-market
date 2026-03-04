package com.minimart.common.logging

import brave.Tracer
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.util.*

/**
 * Trace ID를 관리하는 컨텍스트 홀더
 * Sleuth와 통합하여 분산 추적을 지원합니다.
 */
@Component
class TraceContextHolder(
    private val tracer: Tracer?
) {

    /**
     * 현재 Trace ID를 가져옵니다.
     * Sleuth가 활성화된 경우 Sleuth의 Trace ID를 사용하고,
     * 그렇지 않으면 MDC에서 가져옵니다.
     */
    fun getCurrentTraceId(): String {
        // Sleuth Trace ID 우선
        val currentSpan = tracer?.currentSpan()
        if (currentSpan != null) {
            return currentSpan.context().traceIdString()
        }

        // MDC에서 가져오기
        return MDC.get("traceId") ?: generateTraceId()
    }

    /**
     * 현재 Span ID를 가져옵니다.
     */
    fun getCurrentSpanId(): String {
        val currentSpan = tracer?.currentSpan()
        if (currentSpan != null) {
            return currentSpan.context().spanIdString()
        }

        return MDC.get("spanId") ?: "no-span"
    }

    /**
     * 새로운 Trace ID를 생성하고 MDC에 설정합니다.
     */
    fun generateAndSetTraceId(): String {
        val traceId = generateTraceId()
        MDC.put("traceId", traceId)
        return traceId
    }

    /**
     * 기존 Trace ID를 MDC에 설정합니다.
     */
    fun setTraceId(traceId: String) {
        MDC.put("traceId", traceId)
    }

    /**
     * MDC에서 Trace ID를 제거합니다.
     */
    fun clearTraceId() {
        MDC.remove("traceId")
        MDC.remove("spanId")
    }

    /**
     * 새로운 Trace ID를 생성합니다.
     */
    private fun generateTraceId(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }
}

/**
 * Trace ID를 자동으로 관리하는 블록 실행 함수
 */
inline fun <T> TraceContextHolder.withTraceId(traceId: String? = null, block: () -> T): T {
    val actualTraceId = traceId ?: generateAndSetTraceId()
    setTraceId(actualTraceId)
    try {
        return block()
    } finally {
        clearTraceId()
    }
}