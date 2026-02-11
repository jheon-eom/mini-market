package com.minimarket.accountservice.application.dto

data class CustomerJoinCommand(
    val email: String,
    val password: String,
)

data class CustomerJoinResult(
    val id: Long,
    val accessToken: String,
    val refreshToken: String,
)