package com.minimarket.shippingservice.application.out

interface EventOutBoxWriter {
    fun save(eventId: String, eventType: String, relationId: String)
}