package com.minimarket.catalogtservice.adapter.out.persistence.jpa

import com.minimarket.catalogtservice.adapter.out.persistence.jpa.entity.EventOutBoxEntity
import com.minimarket.catalogtservice.application.out.EventOutBoxWriter
import org.springframework.stereotype.Repository

@Repository
class EventOutBoxJpaWriter(
    private val eventOutBoxRepository: EventOutBoxRepository
): EventOutBoxWriter {
    override fun write(eventId: String, eventType: String, relationId: String) {
        val eventOutBoxEntity = EventOutBoxEntity(
            eventId = eventId,
            eventType = eventType,
            relationId = relationId
        )
        eventOutBoxRepository.save(eventOutBoxEntity)
    }
}