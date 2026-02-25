package com.minimarket.catalogtservice.adapter.`in`.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.minimarket.catalogtservice.application.dto.ProductReserveCommand
import com.minimarket.catalogtservice.application.dto.ProductReserveItem
import com.minimarket.catalogtservice.application.`in`.ProductReserveUseCase
import com.minimart.common.event.EventTopic
import com.minimart.common.event.OrderCreated
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

/**
 * 주문 생성 이벤트를 수신하고 재고 예약을 처리하는 리스너
 */
@Component
class OrderCreatedEventListener(
    private val objectMapper: ObjectMapper,
    private val productReserveUseCase: ProductReserveUseCase,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = [EventTopic.ORDER_CREATED],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(
        maxAttempts = 3,
        backoff = Backoff(delay = 1000, multiplier = 2.0),
        value = [Exception::class]
    )
    fun listen(message: String, acknowledgment: Acknowledgment) {
        logger.info("Received ORDER_CREATED event: $message")

        try {
            val command = readEvent(message)
            logger.info("Processing inventory reservation for orderId: ${command.orderId}, eventId: ${command.eventId}")

            // 재고 예약 처리 (트랜잭션 내에서 아웃박스 저장 및 Spring Event 발행)
            productReserveUseCase.reserve(command)

            // 처리 성공 시 Kafka offset commit
            acknowledgment.acknowledge()
            logger.info("Successfully processed inventory reservation for orderId: ${command.orderId}")
        } catch (e: Exception) {
            logger.error("Failed to process ORDER_CREATED event: $message", e)
            // 예외 발생 시 재시도 (Spring Retry가 처리)
            throw e
        }
    }

    private fun readEvent(message: String): ProductReserveCommand =
        objectMapper.readValue(message, OrderCreated::class.java)
            .let {
                ProductReserveCommand(
                    eventId = it.eventId,
                    orderId = it.orderId,
                    items = it.orderLines.map { item ->
                        ProductReserveItem(
                            productId = item.productId,
                            quantity = item.quantity
                        )
                    }
                )
            }
}