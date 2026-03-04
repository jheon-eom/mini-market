package com.minimarket.orderservice.adapter.out.persistence.jpa

import com.minimarket.orderservice.adapter.out.persistence.jpa.entity.OrderEntity
import com.minimarket.orderservice.adapter.out.persistence.jpa.entity.OrderLineEntity
import com.minimarket.orderservice.adapter.out.persistence.jpa.entity.ShippingInfoEntity
import com.minimarket.orderservice.application.out.OrderWriter
import com.minimarket.orderservice.domain.Order
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class OrderJpaWriter(
    private val repository: OrderRepository,
    private val orderLineRepository: OrderLineRepository,
    private val shippingInfoRepository: ShippingInfoRepository
): OrderWriter {
    override fun save(order: Order): Order {
        val savedOrderEntity = OrderEntity(
            buyerId = order.buyerId.value,
            amount = order.amount,
            status = order.status.name,
            orderedAt = order.orderedAt
        ).let { repository.save(it) }

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

        val savedShippingInfoEntity = order.shippingInfo.let {
            ShippingInfoEntity(
                receiverName = it.receiverName,
                address = it.address,
                detailAddress = it.detailAddress,
                orderId = savedOrderEntity.id.toString(),
            )
        }.let {
            shippingInfoRepository.save(it)
        }

        return savedOrderEntity.toDomain(savedOrderLinesEntity.map { it.toDomain() }, savedShippingInfoEntity)
    }

    override fun update(order: Order) {
        repository.findById(UUID.fromString(order.id!!.value))
            .orElseThrow()
            .apply {
                this.status = order.status.name
            }.let { repository.save(it) }
    }
}