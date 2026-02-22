package com.minimarket.orderservice.application

import com.minimarket.orderservice.application.dto.OrderCreateCommand
import com.minimarket.orderservice.application.dto.OrderCreateResult
import com.minimarket.orderservice.application.`in`.OrderUseCase
import com.minimarket.orderservice.application.out.OrderWriter
import com.minimarket.orderservice.domain.BuyerId
import com.minimarket.orderservice.domain.Order
import com.minimarket.orderservice.domain.OrderLine
import com.minimarket.orderservice.domain.OrderStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OrderCreateService(
    private val orderWriter: OrderWriter
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

        // TODO: 주문 생성 이벤트 발행

        return OrderCreateResult(
            orderId = savedOrder.id!!.value
        )
    }
}