package com.minimart.common.event.application

import java.math.BigDecimal

interface ApplicationEvent {
    val eventId: String
}

data class InventoryReserveCompleteEvent(
    override val eventId: String,
    val orderId: String,
    val isSuccess: Boolean,
    val failedItems: List<String> = emptyList()
): ApplicationEvent

data class OrderStatusUpdatedEvent(
    override val eventId: String,
    val orderId: String,
    val buyerId: Long,
    val orderAmount: BigDecimal,
): ApplicationEvent

data class PaymentProcessedEvent(
    override val eventId: String,
    val orderId: String,
    val paymentId: String,
): ApplicationEvent