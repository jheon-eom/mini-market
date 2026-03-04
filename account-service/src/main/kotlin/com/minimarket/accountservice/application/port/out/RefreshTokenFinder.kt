package com.minimarket.accountservice.application.port.out

import com.minimarket.accountservice.domain.UserId

interface RefreshTokenFinder {
    fun validate(refreshToken: String, userId: UserId): Boolean
}