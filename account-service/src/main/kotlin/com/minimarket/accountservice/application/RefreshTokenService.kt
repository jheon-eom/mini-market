package com.minimarket.accountservice.application

import com.minimarket.accountservice.application.dto.AuthToken
import com.minimarket.accountservice.application.port.`in`.RefreshTokenUseCase
import com.minimarket.accountservice.application.port.out.AuthProvider
import com.minimarket.accountservice.application.port.out.RefreshTokenFinder
import com.minimarket.accountservice.application.port.out.RefreshTokenWriter
import com.minimarket.accountservice.application.port.out.UserFinder
import com.minimarket.accountservice.domain.User
import com.minimarket.accountservice.domain.RefreshToken
import com.minimarket.accountservice.domain.UserId
import com.minimart.common.exception.TokenAuthenticationException
import com.minimart.common.exception.TokenExpiredException
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now

@Service
class RefreshTokenService(
    private val refreshTokenWriter: RefreshTokenWriter,
    private val refreshTokenFinder: RefreshTokenFinder,
    private val authProvider: AuthProvider,
    private val userFinder: UserFinder,
): RefreshTokenUseCase {
    fun save(user: User, refreshToken: String) {
        RefreshToken(
            userId = user.id!!,
            token = refreshToken,
            expiredAt = now().plusDays(7)
        ).let { refreshTokenWriter.save(it) }
    }

    override fun refresh(refreshToken: String, userId: UserId): AuthToken {
        if (!refreshTokenFinder.validate(refreshToken, userId)) throw TokenExpiredException()

        return userFinder.findById(userId)
            .let { authProvider.generate(it) }
            .also { save(userFinder.findById(userId), it.refreshToken) }
    }
}