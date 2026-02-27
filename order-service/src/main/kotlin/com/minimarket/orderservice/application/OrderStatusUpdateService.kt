package com.minimarket.orderservice.application

import com.minimarket.orderservice.adapter.`in`.event.OrderReserveSuccessCommand
import com.minimarket.orderservice.application.`in`.OrderStatusUpdateUseCase
import com.minimarket.orderservice.application.out.EventOutBoxReader
import com.minimarket.orderservice.application.out.EventOutBoxWriter
import com.minimarket.orderservice.application.out.OrderFinder
import com.minimarket.orderservice.application.out.OrderWriter
import com.minimart.common.event.application.OrderStatusUpdatedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderStatusUpdateService(
    private val orderFinder: OrderFinder,
    private val orderWriter: OrderWriter,
    private val eventOutBoxWriter: EventOutBoxWriter,
    private val eventOutBoxReader: EventOutBoxReader,
    private val applicationEventPublisher: ApplicationEventPublisher,
): OrderStatusUpdateUseCase {
    /**
     * 주문 상태를 예약 완료로 업데이트
     * 완료 이후 주문 예약 성공 이벤트 발행
     * 이벤트의 수신자는 결제 서비스와 배송 서비스
     */
    @Transactional
    override fun updateToReserved(command: OrderReserveSuccessCommand) {
        if (eventOutBoxReader.existByEventId(command.eventId)) {
            return
        }

        val order = orderFinder.findById(command.orderId)
        order.reserve()

        orderWriter.update(order)

        eventOutBoxWriter.save(
            command.orderId,
            command.eventType,
            command.orderId
        )

        applicationEventPublisher.publishEvent(
            OrderStatusUpdatedEvent(
                eventId = command.eventId,
                orderId = command.orderId,
                order.amount
            )
        )
    }
}