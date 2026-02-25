package com.minimart.common.event.application

interface ApplicationEvent {
    val eventId: String
}

data class InventoryReserveCompleteEvent(
    override val eventId: String,
    val orderId: String,
    val isSuccess: Boolean,
    val failedItems: List<String> = emptyList()
): ApplicationEvent