package com.minimarket.accountservice.application.dto

data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
)