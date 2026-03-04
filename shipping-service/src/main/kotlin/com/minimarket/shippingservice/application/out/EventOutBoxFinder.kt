package com.minimarket.shippingservice.application.out

interface EventOutBoxFinder {
    fun existsByEventId(eventId: String): Boolean
}