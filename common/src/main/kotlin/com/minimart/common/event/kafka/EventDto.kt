package com.minimart.common.event.kafka

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class OrderCreated(
    override val eventId: String = UUID.randomUUID().toString(),

    override val occurredAt: LocalDateTime = LocalDateTime.now(),

    override val eventType: String = EventTopic.ORDER_CREATED,

    val orderId: String,

    val buyerId: Long,

    val totalAmount: BigDecimal,

    val orderLines: List<OrderLine>
): DomainEvent

data class OrderLine(
    val productId: Long,

    val quantity: Int,

    val price: BigDecimal,

    val amount: BigDecimal
)

data class InventoryReserved(
    override val eventId: String = UUID.randomUUID().toString(),

    override val occurredAt: LocalDateTime = LocalDateTime.now(),

    override val eventType: String = EventTopic.INVENTORY_RESERVED,

    val orderId: String,
): DomainEvent

data class InventoryFailed(
    override val eventId: String = UUID.randomUUID().toString(),

    override val occurredAt: LocalDateTime = LocalDateTime.now(),

    override val eventType: String = EventTopic.INVENTORY_FAILED,

    val orderId: String,
): DomainEvent

data class OrderReserved(
    override val eventId: String = UUID.randomUUID().toString(),

    override val occurredAt: LocalDateTime = LocalDateTime.now(),

    override val eventType: String = EventTopic.ORDER_RESERVED,

    val orderId: String,

    val buyerId: Long,

    val orderAmount: BigDecimal,
): DomainEvent

data class OrderFailed(
    override val eventId: String = UUID.randomUUID().toString(),

    override val occurredAt: LocalDateTime = LocalDateTime.now(),

    override val eventType: String = EventTopic.ORDER_FAILED,

    val orderId: String,

    val orderLines: List<OrderLine>
): DomainEvent

data class PaymentProcessed(
    override val eventId: String = UUID.randomUUID().toString(),

    override val occurredAt: LocalDateTime = LocalDateTime.now(),

    override val eventType: String = EventTopic.PAYMENT_PROCESSED,

    val orderId: String,

    val paymentId: String,
): DomainEvent

data class PaymentFailed(
    override val eventId: String = UUID.randomUUID().toString(),

    override val occurredAt: LocalDateTime = LocalDateTime.now(),

    override val eventType: String = EventTopic.PAYMENT_FAILED,

    val orderId: String
): DomainEvent

data class ShipmentCreated(
    override val eventId: String = UUID.randomUUID().toString(),

    override val occurredAt: LocalDateTime = LocalDateTime.now(),

    override val eventType: String = EventTopic.SHIPPING_CREATED,

    val orderId: String,

    val shipmentId: String,
): DomainEvent

data class ShipmentFailed(
    override val eventId: String = UUID.randomUUID().toString(),

    override val occurredAt: LocalDateTime = LocalDateTime.now(),

    override val eventType: String = EventTopic.SHIPPING_FAILED,

    val orderId: String,

    val shipmentId: String,
): DomainEvent