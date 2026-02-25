package com.minimarket.catalogtservice.adapter.out.persistence.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "event_out_box")
class EventOutBoxEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "event_id", nullable = false, unique = true, updatable = false)
    val eventId: String,

    @Column(name = "event_type", nullable = false, updatable = false)
    val eventType: String,

    @Column(name = "relation_id", nullable = false, updatable = false)
    val relationId: String,

    @Column(name = "processed_at")
    var processedAt: LocalDateTime? = null
) {
    fun markProcessed() {
        this.processedAt = LocalDateTime.now()
    }
}