package com.minimarket.orderservice.application.out

interface EventOutBoxReader {
    fun existByEventId(eventId: String): Boolean
}