package com.minimarket.paymentservice.application.out

interface EventOutBoxWriter {
    fun save(eventId: String, eventType: String, relationId: String)
}