package com.minimarket.orderservice.application

import com.minimarket.orderservice.application.dto.OrderCreateCommand
import com.minimarket.orderservice.application.dto.OrderCreateResult
import com.minimarket.orderservice.application.`in`.OrderUseCase
import com.minimarket.orderservice.application.out.OrderWriter
import com.minimarket.orderservice.domain.BuyerId
import com.minimarket.orderservice.domain.Order
import com.minimarket.orderservice.domain.OrderLine
import com.minimarket.orderservice.domain.OrderStatus
import com.minimart.common.event.EventPublisher
import com.minimart.common.event.EventTopic
import com.minimart.common.event.OrderCreated
import com.minimart.common.event.OrderLineCreated
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OrderCreateService(
    private val orderWriter: OrderWriter,
    private val eventPublisher: EventPublisher
): OrderUseCase {
    @Transactional
    override fun create(command: OrderCreateCommand): OrderCreateResult {
        val savedOrder = Order(
            buyerId = BuyerId(command.buyerId),
            lines = command.orderLines.map {
                OrderLine(
                    productId = it.productId,
                    quantity = it.quantity,
                    price = it.price,
                    amount = it.amount
                )
            },
            amount = command.amount,
            status = OrderStatus.PENDING,
            orderedAt = LocalDateTime.now()
        ).run {
            orderWriter.save(this)
        }

        // 주문 생성 이벤트 발행
        val event = OrderCreated(
            orderId = savedOrder.id!!.value,
            buyerId = savedOrder.buyerId.value,
            totalAmount = savedOrder.amount,
            orderLines = savedOrder.lines.map {
                OrderLineCreated(
                    productId = it.productId,
                    quantity = it.quantity,
                    price = it.price,
                    amount = it.amount
                )
            }
        )

        eventPublisher.publish(EventTopic.ORDER_CREATED, event)

        return OrderCreateResult(
            orderId = savedOrder.id.value
        )
    }
}