package com.minimarket.catalogtservice.adapter.out.persistence.jpa

import com.minimarket.catalogtservice.adapter.out.persistence.jpa.entity.EventOutBoxEntity
import org.springframework.data.jpa.repository.JpaRepository

interface EventOutBoxRepository: JpaRepository<EventOutBoxEntity, Long> {
    fun existsByEventId(eventId: String): Boolean
}