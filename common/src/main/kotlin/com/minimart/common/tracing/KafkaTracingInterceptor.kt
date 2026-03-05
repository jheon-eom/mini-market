package com.minimart.common.tracing

import io.micrometer.tracing.Span
import io.micrometer.tracing.Tracer
import org.apache.kafka.clients.consumer.ConsumerInterceptor
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Kafka Consumer에서 메시지를 받을 때 헤더에서 tracing 정보를 추출하여
 * 현재 Thread의 TraceContext에 복원하는 인터셉터
 */
@Component
class KafkaTracingInterceptor(
    private val tracer: Tracer
) : ConsumerInterceptor<String, Any> {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun onConsume(records: ConsumerRecords<String, Any>): ConsumerRecords<String, Any> {
        records.forEach { record ->
            // Kafka 메시지 헤더에서 traceId/spanId 추출
            val traceIdHeader = record.headers().lastHeader("traceId")
            val spanIdHeader = record.headers().lastHeader("spanId")

            if (traceIdHeader != null && spanIdHeader != null) {
                val traceId = String(traceIdHeader.value())
                val spanId = String(spanIdHeader.value())

                // 새로운 span 생성하여 tracing context 복원
                val span: Span = tracer.nextSpan()
                    .name("kafka-consume")
                    .tag("kafka.topic", record.topic())
                    .tag("kafka.partition", record.partition().toString())
                    .tag("kafka.offset", record.offset().toString())
                    .tag("restored.traceId", traceId)
                    .tag("restored.spanId", spanId)
                    .start()

                logger.debug(
                    "Kafka 메시지 tracing 정보 복원 - Topic: {}, TraceId: {}, SpanId: {}",
                    record.topic(),
                    traceId,
                    spanId
                )

                // Span을 현재 context에 설정
                tracer.withSpan(span)
            }
        }
        return records
    }

    override fun onCommit(offsets: MutableMap<TopicPartition, OffsetAndMetadata>?) {
        // 커밋 후 처리 로직 (필요시 구현)
    }

    override fun close() {
        // 리소스 정리 (필요시 구현)
    }

    override fun configure(configs: MutableMap<String, *>?) {
        // 설정 (필요시 구현)
    }
}