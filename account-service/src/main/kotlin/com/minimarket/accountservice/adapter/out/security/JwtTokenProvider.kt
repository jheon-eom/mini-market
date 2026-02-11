package com.minimarket.accountservice.adapter.out.security

import com.minimarket.accountservice.application.dto.AuthToken
import com.minimarket.accountservice.application.port.out.AuthProvider
import com.minimart.common.security.JwtProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

/**
 * JWT 토큰 생성 전용 컴포넌트
 * account-service에서만 사용 (인증 전담 서비스)
 * 토큰 검증은 common 모듈의 JwtValidator 사용
 */
@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) : AuthProvider {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    override fun generate(userId: Long, email: String, role: String): AuthToken {
        val now = Date()

        val accessToken = Jwts.builder()
            .setSubject(userId.toString())
            .claim("email", email)
            .claim("type", "access")
            .claim("role", listOf(role))
            .setIssuedAt(now)
            .setExpiration(Date(now.time + jwtProperties.accessTokenValidity))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()

        val refreshToken = Jwts.builder()
            .setSubject(userId.toString())
            .claim("type", "refresh")
            .setIssuedAt(now)
            .setExpiration(Date(now.time + jwtProperties.refreshTokenValidity))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()

        return AuthToken(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }
}