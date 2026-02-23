package com.minimarket.catalogtservice.adapter.`in`.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.minimarket.catalogtservice.application.dto.ProductReserveCommand
import com.minimarket.catalogtservice.application.dto.ProductReserveItem
import com.minimarket.catalogtservice.application.`in`.ProductReserveUseCase
import com.minimart.common.event.EventListener
import com.minimart.common.event.EventPublisher
import com.minimart.common.event.OrderCreated
import com.minimart.common.event.EventTopic
import com.minimart.common.event.InventoryReserved
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class OrderCreatedEventListener(
    private val objectMapper: ObjectMapper,
    private val productReserveUseCase: ProductReserveUseCase,
    private val eventPublisher: EventPublisher
): EventListener<OrderCreated> {
    @KafkaListener(
        topics = [EventTopic.ORDER_CREATED],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listen(message: String, acknowledgment: Acknowledgment) {
        try {
            val event = objectMapper.readValue(message, OrderCreated::class.java)
            handleEvent(event)
            acknowledgment.acknowledge()
        } catch (e: Exception) {
            // 에러 처리 로직 (재시도, DLQ 전송 등)
            // 예: throw e // 재시도를 위해 예외를 던질 수 있음
        }
    }

    override fun handleEvent(event: OrderCreated) {
        val result = productReserveUseCase.reserve(
            ProductReserveCommand(
                orderId = event.orderId,
                items = event.orderLines.map { item ->
                    ProductReserveItem(
                        productId = item.productId,
                        quantity = item.quantity
                    )
                }
            )
        )

        if (result.success) {
            eventPublisher.publish(
                EventTopic.INVENTORY_RESERVED,
                event.orderId,
                InventoryReserved(orderId = event.orderId)
            )
        } else {
            // 예약 실패 시 추가 처리 로직 (예: 주문 취소 이벤트 발행 등)
            //InventoryFailed
        }
    }
}