package com.minimart.common.event

import com.minimart.common.event.DomainEvent
import com.minimart.common.event.EventPublisher
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

/**
 * Kafka를 이용한 이벤트 발행 구현체
 */
@Component
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>
): EventPublisher {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun publish(topic: String, partitionKey: String, event: DomainEvent) {
        val future: CompletableFuture<SendResult<String, Any>> =
            kafkaTemplate.send(topic, partitionKey, event)

        future.whenComplete { result, ex ->
            if (ex == null) {
                logger.info(
                    "이벤트 발행 성공 - Topic: {}, Key: {}, EventType: {}, Partition: {}, Offset: {}",
                    topic,
                    partitionKey,
                    event.eventType,
                    result.recordMetadata.partition(),
                    result.recordMetadata.offset()
                )
            } else {
                logger.error(
                    "이벤트 발행 실패 - Topic: {}, Key: {}, EventType: {}",
                    topic,
                    partitionKey,
                    event.eventType,
                    ex
                )
            }
        }
    }
}