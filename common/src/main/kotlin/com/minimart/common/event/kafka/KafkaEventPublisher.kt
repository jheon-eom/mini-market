package com.minimart.common.event.kafka

import com.minimart.common.tracing.TraceContextHolder
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.kafka.support.SendResult
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

/**
 * Kafka를 이용한 이벤트 발행 구현체
 * TraceId/SpanId를 자동으로 주입하고 Kafka 헤더에 전파
 */
@Component
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val traceContextHolder: TraceContextHolder
): EventPublisher {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun publish(topic: String, partitionKey: String, event: DomainEvent) {
        // 현재 trace context에서 traceId와 spanId 추출
        val traceId = event.traceId ?: traceContextHolder.getCurrentTraceId()
        val spanId = event.spanId ?: traceContextHolder.getCurrentSpanId()

        // 이벤트에 tracing 정보 주입 (copy를 통해 새 인스턴스 생성)
        val tracedEvent = when (event) {
            is OrderCreated -> event.copy(traceId = traceId, spanId = spanId)
            is InventoryReserved -> event.copy(traceId = traceId, spanId = spanId)
            is InventoryFailed -> event.copy(traceId = traceId, spanId = spanId)
            is OrderReserved -> event.copy(traceId = traceId, spanId = spanId)
            is OrderFailed -> event.copy(traceId = traceId, spanId = spanId)
            is PaymentProcessed -> event.copy(traceId = traceId, spanId = spanId)
            is PaymentFailed -> event.copy(traceId = traceId, spanId = spanId)
            is ShippingCreated -> event.copy(traceId = traceId, spanId = spanId)
            else -> event
        }

        // Kafka 메시지 헤더에도 tracing 정보 추가
        val message: Message<DomainEvent> = MessageBuilder
            .withPayload(tracedEvent)
            .setHeader(KafkaHeaders.KEY, partitionKey)
            .setHeader(KafkaHeaders.TOPIC, topic)
            .apply {
                traceId?.let { setHeader("traceId", it) }
                spanId?.let { setHeader("spanId", it) }
            }
            .build()

        val future: CompletableFuture<SendResult<String, Any>> =
            kafkaTemplate.send(message)

        future.whenComplete { result, ex ->
            if (ex == null) {
                logger.info(
                    "이벤트 발행 성공 - Topic: {}, Key: {}, EventType: {}, TraceId: {}, SpanId: {}, Partition: {}, Offset: {}",
                    topic,
                    partitionKey,
                    tracedEvent.eventType,
                    traceId,
                    spanId,
                    result.recordMetadata.partition(),
                    result.recordMetadata.offset()
                )
            } else {
                logger.error(
                    "이벤트 발행 실패 - Topic: {}, Key: {}, EventType: {}, TraceId: {}, SpanId: {}",
                    topic,
                    partitionKey,
                    tracedEvent.eventType,
                    traceId,
                    spanId,
                    ex
                )
            }
        }
    }
}