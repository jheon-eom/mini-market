package com.minimarket.shippingservice.adapter.`in`.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.minimarket.shippingservice.application.dto.ShippingCreateCommand
import com.minimarket.shippingservice.application.`in`.ShippingCreateUseCase
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.OrderReserved
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class OrderReservedEventListener(
    private val objectMapper: ObjectMapper,
    private val shippingCreateUseCase: ShippingCreateUseCase
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    @KafkaListener(
        topics = [EventTopic.ORDER_RESERVED],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 500L)
    )
    fun createShipping(message: String) {
        val event = objectMapper.readValue(message, OrderReserved::class.java)
        logger.info("[Shipping] ORDER_RESERVED 이벤트 수신 - orderId: ${event.orderId}, receiver: ${event.shippingInfo.receiverName}, traceId: ${event.traceId}")

        shippingCreateUseCase.create(
            ShippingCreateCommand(
                eventId = event.eventId,
                orderId = event.orderId,
                receiverName =  event.shippingInfo.receiverName,
                address = event.shippingInfo.address,
                detailAddress = event.shippingInfo.detailAddress,
            )
        )

        logger.info("[Shipping] 배송 생성 요청 완료 - orderId: ${event.orderId}")
    }
}