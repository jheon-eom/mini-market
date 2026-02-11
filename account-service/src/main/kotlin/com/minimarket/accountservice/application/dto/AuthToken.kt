package com.minimarket.accountservice.application.dto

data class AuthToken(
    val accessToken: String,
    val refreshToken: String, // TODO: refreshToken 검증 로직 추가 필요
)