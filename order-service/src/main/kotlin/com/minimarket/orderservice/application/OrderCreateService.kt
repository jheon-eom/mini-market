package com.minimarket.orderservice.application

import com.minimarket.orderservice.application.dto.OrderCreateCommand
import com.minimarket.orderservice.application.dto.OrderCreateResult
import com.minimarket.orderservice.application.`in`.OrderUseCase
import com.minimarket.orderservice.application.out.OrderWriter
import com.minimarket.orderservice.domain.BuyerId
import com.minimarket.orderservice.domain.Order
import com.minimarket.orderservice.domain.OrderLine
import com.minimarket.orderservice.domain.OrderStatus
import com.minimarket.orderservice.domain.ShippingInfo
import com.minimart.common.event.kafka.EventPublisher
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.OrderCreated
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OrderCreateService(
    private val orderWriter: OrderWriter,
    private val eventPublisher: EventPublisher
): OrderUseCase {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun create(command: OrderCreateCommand): OrderCreateResult {
        logger.info("[Order] 주문 생성 시작 - buyerId: ${command.buyerId}, amount: ${command.amount}, items: ${command.orderLines.size}")
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
            orderedAt = LocalDateTime.now(),
            shippingInfo = ShippingInfo(
                receiverName = command.shippingInfo.receiverName,
                address = command.shippingInfo.address,
                detailAddress = command.shippingInfo.detailAddress,
            )
        ).run {
            orderWriter.save(this)
        }

        logger.info("[Order] 주문 저장 완료 - orderId: ${savedOrder.id!!.value}, status: ${savedOrder.status}")

        // 주문 생성 이벤트 발행
        val event = OrderCreated(
            orderId = savedOrder.id!!.value,
            buyerId = savedOrder.buyerId.value,
            totalAmount = savedOrder.amount,
            orderLines = savedOrder.lines.map {
                com.minimart.common.event.kafka.OrderLine(
                    productId = it.productId,
                    quantity = it.quantity,
                    price = it.price,
                    amount = it.amount
                )
            }
        )

        eventPublisher.publish(
            EventTopic.ORDER_CREATED,
            savedOrder.id.value,
            event
        )

        logger.info("[Order] ORDER_CREATED 이벤트 발행 완료 - orderId: ${savedOrder.id.value}")

        return OrderCreateResult(
            orderId = savedOrder.id.value
        )
    }
}