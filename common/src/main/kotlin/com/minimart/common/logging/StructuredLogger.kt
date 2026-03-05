package com.minimart.common.logging

import com.fasterxml.jackson.databind.ObjectMapper
import io.micrometer.tracing.Tracer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component

/**
 * 구조화된 로깅을 위한 유틸리티 클래스
 * JSON 형식의 로그를 생성하며, Trace ID를 자동으로 포함합니다.
 */
@Component
class StructuredLogger(
    private val objectMapper: ObjectMapper,
    private val tracer: Tracer?
) {

    fun logEvent(
        logger: Logger,
        level: LogLevel,
        eventName: String,
        message: String,
        additionalFields: Map<String, Any> = emptyMap()
    ) {
        val logData = buildLogData(eventName, message, additionalFields)

        when (level) {
            LogLevel.TRACE -> logger.trace(objectMapper.writeValueAsString(logData))
            LogLevel.DEBUG -> logger.debug(objectMapper.writeValueAsString(logData))
            LogLevel.INFO -> logger.info(objectMapper.writeValueAsString(logData))
            LogLevel.WARN -> logger.warn(objectMapper.writeValueAsString(logData))
            LogLevel.ERROR -> logger.error(objectMapper.writeValueAsString(logData))
        }
    }

    fun logEventWithThrowable(
        logger: Logger,
        level: LogLevel,
        eventName: String,
        message: String,
        throwable: Throwable,
        additionalFields: Map<String, Any> = emptyMap()
    ) {
        val logData = buildLogData(eventName, message, additionalFields).toMutableMap()
        logData["error_type"] = throwable.javaClass.simpleName
        logData["error_message"] = throwable.message ?: "No error message"

        when (level) {
            LogLevel.TRACE -> logger.trace(objectMapper.writeValueAsString(logData), throwable)
            LogLevel.DEBUG -> logger.debug(objectMapper.writeValueAsString(logData), throwable)
            LogLevel.INFO -> logger.info(objectMapper.writeValueAsString(logData), throwable)
            LogLevel.WARN -> logger.warn(objectMapper.writeValueAsString(logData), throwable)
            LogLevel.ERROR -> logger.error(objectMapper.writeValueAsString(logData), throwable)
        }
    }

    private fun buildLogData(
        eventName: String,
        message: String,
        additionalFields: Map<String, Any>
    ): Map<String, Any> {
        val traceId = getCurrentTraceId()
        val spanId = getCurrentSpanId()

        return mutableMapOf<String, Any>(
            "event" to eventName,
            "message" to message,
            "service" to (System.getenv("SERVICE_NAME") ?: "unknown"),
            "trace_id" to traceId,
            "span_id" to spanId
        ).apply {
            putAll(additionalFields)
        }
    }

    private fun getCurrentTraceId(): String {
        // Sleuth의 Trace ID 가져오기
        val currentSpan = tracer?.currentSpan()
        if (currentSpan != null) {
            return currentSpan.context().spanId()
        }

        // MDC에서 가져오기
        return MDC.get("traceId") ?: "no-trace"
    }

    private fun getCurrentSpanId(): String {
        val currentSpan = tracer?.currentSpan()
        if (currentSpan != null) {
            return currentSpan.context().spanId()
        }

        return MDC.get("spanId") ?: "no-span"
    }
}

enum class LogLevel {
    TRACE, DEBUG, INFO, WARN, ERROR
}

/**
 * Kotlin DSL 스타일의 로깅을 위한 확장 함수
 */
inline fun <reified T> T.getStructuredLogger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}

/**
 * 간편한 이벤트 로깅을 위한 확장 함수
 */
fun Logger.logEvent(
    structuredLogger: StructuredLogger,
    level: LogLevel,
    eventName: String,
    message: String,
    fields: Map<String, Any> = emptyMap()
) {
    structuredLogger.logEvent(this, level, eventName, message, fields)
}