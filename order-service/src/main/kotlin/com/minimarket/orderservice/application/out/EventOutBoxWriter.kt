package com.minimarket.orderservice.application.out

interface EventOutBoxWriter {
    fun save(eventId: String, eventType: String, relationId: String)
}