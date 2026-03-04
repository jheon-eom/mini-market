package com.minimarket.shippingservice.adapter.out.persistence.jpa

import com.minimarket.shippingservice.adapter.out.persistence.jpa.entity.EventOutBoxEntity
import com.minimarket.shippingservice.application.out.EventOutBoxWriter
import org.springframework.stereotype.Repository

@Repository
class EventOutBoxJpaWriter(
    private val eventOutBoxRepository: EventOutBoxRepository
): EventOutBoxWriter {
    override fun save(eventId: String, eventType: String, relationId: String) {
        val eventOutBoxEntity = EventOutBoxEntity(
            eventId = eventId,
            eventType = eventType,
            relationId = relationId
        )
        eventOutBoxRepository.save(eventOutBoxEntity)
    }
}