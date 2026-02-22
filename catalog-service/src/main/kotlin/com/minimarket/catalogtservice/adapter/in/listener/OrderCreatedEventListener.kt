package com.minimarket.catalogtservice.adapter.`in`.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.minimart.common.event.EventListener
import com.minimart.common.event.OrderCreated
import com.minimart.common.event.EventTopic
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class OrderCreatedEventListener(
    private val objectMapper: ObjectMapper
): EventListener<OrderCreated> {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = [EventTopic.ORDER_CREATED],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listen(message: String, acknowledgment: Acknowledgment) {
        try {
            val event = objectMapper.readValue(message, OrderCreated::class.java)
            logger.info("주문 생성 이벤트 수신 시작 - OrderId: {}, BuyerId: {}", event.orderId, event.buyerId)

            handleEvent(event)

            acknowledgment.acknowledge()

            logger.info("주문 생성 이벤트 처리 완료 - OrderId: {}", event.orderId)
        } catch (e: Exception) {
            logger.error("주문 생성 이벤트 처리 실패 - Message: {}", message, e)
            // 에러 처리 로직 (재시도, DLQ 전송 등)
            // 예: throw e // 재시도를 위해 예외를 던질 수 있음
        }
    }

    override fun handleEvent(event: OrderCreated) {
        // 주문 생성에 따른 재고 관리 Command를 생성해서 UseCase로 전달하는 로직 구현
    }
}