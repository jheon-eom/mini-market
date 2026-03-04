package com.minimarket.accountservice.adapter.out.persistence.redis

import java.time.LocalDateTime

data class RefreshTokenData(
    val userId: Long,
    val token: String,
    val expiredAt: LocalDateTime
) {
    companion object {
        const val KEY = "user:refresh_token:"

        fun generateKey(userId: Long): String {
            return "$KEY$userId"
        }
    }
}
