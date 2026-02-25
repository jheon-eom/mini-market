package com.minimarket.catalogtservice.application.out

import java.time.LocalDateTime

interface EventOutBoxWriter {
    fun write(eventId: String, eventType: String, relationId: String, processedAt: LocalDateTime? = null)
}