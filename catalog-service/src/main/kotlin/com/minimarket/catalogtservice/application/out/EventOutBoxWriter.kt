package com.minimarket.catalogtservice.application.out

interface EventOutBoxWriter {
    fun write(eventId: String, eventType: String, relationId: String)
}