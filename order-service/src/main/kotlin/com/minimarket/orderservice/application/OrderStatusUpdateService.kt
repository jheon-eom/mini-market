package com.minimarket.orderservice.application

import com.minimarket.orderservice.adapter.`in`.event.OrderReserveFailedCommand
import com.minimarket.orderservice.adapter.`in`.event.OrderReserveSuccessCommand
import com.minimarket.orderservice.application.dto.OrderPaymentFailedCommand
import com.minimarket.orderservice.application.dto.OrderPaymentSuccessCommand
import com.minimarket.orderservice.application.`in`.OrderStatusUpdateUseCase
import com.minimarket.orderservice.application.out.EventOutBoxReader
import com.minimarket.orderservice.application.out.EventOutBoxWriter
import com.minimarket.orderservice.application.out.OrderFinder
import com.minimarket.orderservice.application.out.OrderWriter
import com.minimarket.orderservice.domain.OrderId
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.OrderFailed
import com.minimart.common.event.kafka.OrderLine
import com.minimart.common.event.kafka.OrderReserved
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrderStatusUpdateService(
    private val orderFinder: OrderFinder,
    private val orderWriter: OrderWriter,
    private val eventOutBoxWriter: EventOutBoxWriter,
    private val eventOutBoxReader: EventOutBoxReader,
    private val applicationEventPublisher: ApplicationEventPublisher,
): OrderStatusUpdateUseCase {
    private val logger = LoggerFactory.getLogger(OrderStatusUpdateService::class.java)

    /**
     * 주문 상태를 예약 완료로 업데이트
     * 완료 이후 주문 예약 성공 이벤트 발행
     * 이벤트의 수신자는 결제 서비스와 배송 서비스
     */
    override fun updateToReserved(command: OrderReserveSuccessCommand) {
        logger.info("Reserving order with id: ${command.orderId}")

        if (eventOutBoxReader.existByEventId(command.eventId)) {
            return
        }

        val order = orderFinder.findById(command.orderId)
        order.reserve()

        orderWriter.update(order)

        eventOutBoxWriter.save(
            command.eventId,
            command.eventType,
            command.orderId
        )

        applicationEventPublisher.publishEvent(
            OrderReserved(
                orderId = command.orderId,
                buyerId = order.buyerId.value,
                orderAmount = order.amount,
            )
        )
    }

    /**
     * 주문 예약 실패
     * 주문의 상태를 실패로 업데이트
     */
    override fun updateToReserveFail(command: OrderReserveFailedCommand) {
        logger.info("Reserving order failed with id: ${command.orderId}")

        if (eventOutBoxReader.existByEventId(command.eventId)) {
            return
        }

        val order = orderFinder.findById(command.orderId)
        order.reserveFail()

        orderWriter.update(order)
    }

    override fun updateToPaymentSuccess(command: OrderPaymentSuccessCommand) {
        if (eventOutBoxReader.existByEventId(command.eventId)) {
            return
        }

        val order = orderFinder.findById(command.orderId.value)
        order.pay()

        orderWriter.update(order)

        eventOutBoxWriter.save(
            eventId = command.eventId,
            eventType = EventTopic.PAYMENT_PROCESSED,
            relationId = command.orderId.value
        )
    }

    override fun updateToPaymentFailed(command: OrderPaymentFailedCommand) {
        if (eventOutBoxReader.existByEventId(command.eventId)) {
            return
        }

        val order = orderFinder.findByIdWithOrderLines(command.orderId.value)
        order.payFail()

        orderWriter.update(order)

        eventOutBoxWriter.save(
            eventId = command.eventId,
            eventType = EventTopic.PAYMENT_PROCESSED,
            relationId = command.orderId.value
        )

        // 주문 실패 이벤트 발행
        applicationEventPublisher.publishEvent(
            OrderFailed(
                orderId = command.orderId.value,
                orderLines = order.lines.map {
                    OrderLine(
                        productId = it.productId,
                        quantity = it.quantity,
                        price = it.price,
                        amount = it.amount
                    )
                }
            )
        )
    }
}