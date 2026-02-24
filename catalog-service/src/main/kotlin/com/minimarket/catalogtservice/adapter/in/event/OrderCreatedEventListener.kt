package com.minimarket.catalogtservice.adapter.`in`.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.minimarket.catalogtservice.adapter.out.persistence.jpa.EventOutBoxRepository
import com.minimarket.catalogtservice.application.dto.ProductReserveCommand
import com.minimarket.catalogtservice.application.dto.ProductReserveItem
import com.minimarket.catalogtservice.application.`in`.ProductReserveUseCase
import com.minimart.common.event.EventTopic
import com.minimart.common.event.OrderCreated
import com.minimart.common.exception.OutBoxWriteException
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class OrderCreatedEventListener(
    private val objectMapper: ObjectMapper,
    private val productReserveUseCase: ProductReserveUseCase,
) {
    @KafkaListener(
        topics = [EventTopic.ORDER_CREATED],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listen(message: String, acknowledgment: Acknowledgment) {
        try {
            productReserveUseCase.reserve(readEvent(message))
            acknowledgment.acknowledge()
        } catch (e: OutBoxWriteException) {

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