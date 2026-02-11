package com.minimarket.accountservice.application.dto

import com.minimarket.accountservice.domain.UserRole

data class JoinCommand(
    val email: String,
    val password: String,
    val role: UserRole,
)

data class JoinResult(
    val id: Long,
    val accessToken: String,
    val refreshToken: String,
)