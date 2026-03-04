package com.minimarket.accountservice.adapter.out.persistence.redis

import com.minimarket.accountservice.adapter.out.persistence.redis.RefreshTokenData.Companion.generateKey
import com.minimarket.accountservice.application.port.out.RefreshTokenWriter
import com.minimarket.accountservice.domain.RefreshToken
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime

@Component
class RefreshTokenRedisWriter(
    private val redisTemplate: RedisTemplate<String, Any>
): RefreshTokenWriter {
    override fun save(token: RefreshToken) {
        val key = generateKey(token.userId.value)
        val value = RefreshTokenData(
            userId = token.userId.value,
            token = token.token,
            expiredAt = token.expiredAt
        )

        val ttl = Duration.between(LocalDateTime.now(), token.expiredAt)

        redisTemplate.opsForValue().set(key, value, ttl)
    }
}