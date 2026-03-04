package com.minimarket.accountservice.adapter.out.persistence.redis

import com.minimarket.accountservice.application.port.out.RefreshTokenFinder
import com.minimarket.accountservice.domain.UserId
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RefreshTokenRedisFinder(
    private val redisTemplate: RedisTemplate<String, Any>
): RefreshTokenFinder {
    override fun validate(refreshToken: String, userId: UserId): Boolean {
        val tokenData = redisTemplate.opsForValue()
            .get(RefreshTokenData.generateKey(userId.value)) as? RefreshTokenData
            ?: return false

        return tokenData.token == refreshToken
    }
}