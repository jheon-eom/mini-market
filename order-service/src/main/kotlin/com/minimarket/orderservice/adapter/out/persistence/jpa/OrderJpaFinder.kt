package com.minimarket.orderservice.adapter.out.persistence.jpa

import com.minimarket.orderservice.application.out.OrderFinder
import com.minimarket.orderservice.domain.Order
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class OrderJpaFinder(
    private val orderRepository: OrderRepository,
    private val orderLineRepository: OrderLineRepository
): OrderFinder {
    override fun findById(id: String): Order =
        orderRepository.findById(UUID.fromString(id))
            .orElseThrow()
            .toDomainWithoutOrderLines()

    override fun findByIdWithOrderLines(id: String): Order {
        val orderEntity = orderRepository.findById(UUID.fromString(id))
            .orElseThrow()

        val orderLineEntities = orderLineRepository.findByOrderId(orderEntity.id.toString())

        return orderEntity.toDomain(orderLineEntities.map { it.toDomain() })
    }
}