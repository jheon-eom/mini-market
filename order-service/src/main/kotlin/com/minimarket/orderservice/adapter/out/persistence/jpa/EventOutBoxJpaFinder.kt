package com.minimarket.orderservice.adapter.out.persistence.jpa

import com.minimarket.orderservice.application.out.EventOutBoxReader
import org.springframework.stereotype.Repository

@Repository
class EventOutBoxJpaFinder(
    private val repository: EventOutBoxRepository
): EventOutBoxReader {
    override fun existByEventId(eventId: String): Boolean {
        return repository.existsByEventId(eventId)
    }
}