package com.minimarket.orderservice.adapter.out.persistence.jpa

import com.minimarket.orderservice.adapter.out.persistence.jpa.entity.OrderEntity
import com.minimarket.orderservice.adapter.out.persistence.jpa.entity.OrderLineEntity
import com.minimarket.orderservice.application.out.OrderWriter
import com.minimarket.orderservice.domain.Order
import org.springframework.stereotype.Repository

@Repository
class OrderJpaWriter(
    private val orderRepository: OrderRepository,
    private val orderLineRepository: OrderLineRepository
): OrderWriter {
    override fun save(order: Order): Order {
        val savedOrderEntity = OrderEntity(
            buyerId = order.buyerId.value,
            amount = order.amount,
            status = order.status.name,
            orderedAt = order.orderedAt
        ).let { orderRepository.save(it) }

        val savedOrderLinesEntity = order.lines.map {
            OrderLineEntity(
                productId = it.productId,
                quantity = it.quantity,
                price = it.price,
                amount = it.amount,
                orderId = savedOrderEntity.id.toString(),
            )
        }.let {
            orderLineRepository.saveAll(it)
        }

        return savedOrderEntity.toDomain(savedOrderLinesEntity.map { it.toDomain() })
    }

    override fun update(order: Order) {
        orderRepository.updateById(order.id!!.value)
    }
}