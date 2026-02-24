package com.minimarket.catalogtservice.application.out

interface EventOutBoxFinder {
    fun existsByEventId(eventId: String): Boolean
}