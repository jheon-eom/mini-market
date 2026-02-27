package com.minimarket.orderservice.adapter.out.persistence.jpa

import com.minimarket.orderservice.adapter.out.persistence.jpa.entity.EventOutBoxEntity
import com.minimarket.orderservice.application.out.EventOutBoxWriter
import org.springframework.stereotype.Repository

@Repository
class EventOutBoxJpaWriter(
    private val repository: EventOutBoxRepository
): EventOutBoxWriter {
    override fun save(eventId: String, eventType: String, orderId: String) {
        val entity = EventOutBoxEntity(
            eventId = eventId,
            eventType = eventType,
            relationId = orderId
        )
        repository.save(entity)
    }
}