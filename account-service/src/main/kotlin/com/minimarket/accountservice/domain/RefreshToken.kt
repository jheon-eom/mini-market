package com.minimarket.accountservice.domain

import java.time.LocalDateTime

class RefreshToken(
    val userId: UserId,

    val token: String,

    val expiredAt: LocalDateTime,
)