package com.minimarket.shippingservice.application

import com.minimarket.shippingservice.application.dto.ShippingCreateCommand
import com.minimarket.shippingservice.application.`in`.ShippingCreateUseCase
import com.minimarket.shippingservice.application.out.EventOutBoxFinder
import com.minimarket.shippingservice.application.out.EventOutBoxWriter
import com.minimarket.shippingservice.application.out.ShippingWriter
import com.minimarket.shippingservice.domain.Shipping
import com.minimarket.shippingservice.domain.ShippingStatus
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.ShippingCreated
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ShippingCreateService(
    private val eventOutBoxFinder: EventOutBoxFinder,
    private val eventOutBoxWriter: EventOutBoxWriter,
    private val shippingWriter: ShippingWriter,
    private val eventPublisher: ApplicationEventPublisher
): ShippingCreateUseCase {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun create(command: ShippingCreateCommand) {
        logger.info("[Shipping] 배송 생성 시작 - orderId: ${command.orderId}, receiver: ${command.receiverName}")

        if (eventOutBoxFinder.existsByEventId(command.eventId)) {
            logger.info("[Shipping] 중복 이벤트 무시 - eventId: ${command.eventId}")
            return
        }

        val savedShipping = Shipping(
            orderId = command.orderId,
            receiverName = command.receiverName,
            address = command.address,
            detailAddress = command.detailAddress,
            status = ShippingStatus.PREPARING
        ).let {
            shippingWriter.save(it)
        }

        eventOutBoxWriter.save(
            eventId = command.eventId,
            eventType = EventTopic.SHIPPING_CREATED,
            relationId = savedShipping.id!!.value.toString()
        )


        // Application Event 발행
        eventPublisher.publishEvent(
            ShippingCreated(
                orderId = command.orderId,
                shippingId = savedShipping.id.value
            )
        )

        logger.info("[Shipping] 배송 생성 완료, SHIPPING_CREATED 이벤트 발행 - orderId: ${command.orderId}, shippingId: ${savedShipping.id.value}, status: ${savedShipping.status}")
    }
}