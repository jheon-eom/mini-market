package com.minimarket.shippingservice.adapter.out.persistence.jpa

import com.minimarket.shippingservice.application.out.EventOutBoxFinder
import org.springframework.stereotype.Component

@Component
class EventOutBoxJpaFinder(
    private val eventOutBoxRepository: EventOutBoxRepository
): EventOutBoxFinder {
    override fun existsByEventId(eventId: String): Boolean {
        return eventOutBoxRepository.existsByEventId(eventId)
    }
}