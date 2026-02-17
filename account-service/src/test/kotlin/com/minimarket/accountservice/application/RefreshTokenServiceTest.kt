package com.minimarket.accountservice.application

import com.minimarket.accountservice.adapter.out.persistence.redis.RefreshTokenData
import com.minimarket.accountservice.adapter.out.persistence.redis.RefreshTokenRedisFinder
import com.minimarket.accountservice.adapter.out.persistence.redis.RefreshTokenRedisWriter
import com.minimarket.accountservice.application.port.out.AuthProvider
import com.minimarket.accountservice.application.port.out.RefreshTokenFinder
import com.minimarket.accountservice.application.port.out.UserFinder
import com.minimarket.accountservice.config.RedisConfig
import com.minimarket.accountservice.config.redis.RedisTestConfig
import com.minimarket.accountservice.domain.Email
import com.minimarket.accountservice.domain.User
import com.minimarket.accountservice.domain.UserId
import com.minimarket.accountservice.domain.UserRole
import com.minimart.common.exception.TokenExpiredException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        RefreshTokenService::class,
        RefreshTokenRedisWriter::class,
        RefreshTokenRedisFinder::class,
        RedisTestConfig::class,
        RedisConfig::class,
    ]
)
class RefreshTokenServiceTest {
    @Autowired private lateinit var refreshTokenService: RefreshTokenService
    @Autowired private lateinit var refreshTokenFinder: RefreshTokenFinder
    @Autowired private lateinit var redisTemplate: RedisTemplate<String, Any>
    @MockitoBean private lateinit var userFinder: UserFinder
    @MockitoBean private lateinit var authProvider: AuthProvider

    private val userId = UserId(1L)
    private val user = User(
        id = userId,
        email = Email("test@example.com"),
        passwordHash = "hashed",
        role = UserRole.CUSTOMER,
    )

    @AfterEach
    fun cleanup() {
        redisTemplate.connectionFactory?.connection?.serverCommands()?.flushAll()
    }

    @Test
    fun `save 호출 시 refreshToken이 Redis에 저장된다`() {
        // given
        val token = "sample-refresh-token"

        // when
        refreshTokenService.save(user, token)

        // then: 키가 존재하고 TTL이 설정되어 있는지 확인
        val key = RefreshTokenData.generateKey(userId.value)
        val ttl = redisTemplate.getExpire(key)

        assertThat(ttl).isGreaterThan(0)

        // then: 저장된 token 값이 올바른지 RefreshTokenFinder를 통해 검증
        assertThat(refreshTokenFinder.validate(token, userId)).isTrue()
    }

    @Test
    fun `Redis에 존재하지 않는 refreshToken으로 refresh 호출 시 TokenExpiredException이 발생한다`() {
        // given
        val nonExistentToken = "non-existent-token"

        // when & then
        assertThrows(TokenExpiredException::class.java) {
            refreshTokenService.refresh(nonExistentToken, userId)
        }
    }
}