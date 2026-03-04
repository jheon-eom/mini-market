package com.minimarket.paymentservice.application.out

interface EventOutBoxFinder {
    fun existsByEventId(eventId: String): Boolean
}