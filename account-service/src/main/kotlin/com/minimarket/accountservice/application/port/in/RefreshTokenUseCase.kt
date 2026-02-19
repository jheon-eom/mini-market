package com.minimarket.accountservice.application.port.`in`

import com.minimarket.accountservice.application.dto.AuthToken
import com.minimarket.accountservice.domain.UserId

interface RefreshTokenUseCase {
    fun refresh(userId: UserId, refreshToken: String): AuthToken
}