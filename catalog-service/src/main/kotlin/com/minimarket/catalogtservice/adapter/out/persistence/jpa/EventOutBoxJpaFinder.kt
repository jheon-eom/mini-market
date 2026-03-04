package com.minimarket.catalogtservice.adapter.out.persistence.jpa

import com.minimarket.catalogtservice.application.out.EventOutBoxFinder
import org.springframework.stereotype.Repository

@Repository
class EventOutBoxJpaFinder(
    private val eventOutBoxRepository: EventOutBoxRepository
): EventOutBoxFinder {
    override fun existsByEventId(eventId: String): Boolean {
        return eventOutBoxRepository.existsByEventId(eventId)
    }
}