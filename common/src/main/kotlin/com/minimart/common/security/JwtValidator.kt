package com.minimart.common.security

import com.minimart.common.exception.TokenAuthenticationException
import com.minimart.common.exception.TokenExpiredException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import javax.crypto.SecretKey

/**
 * JWT 검증 전용 컴포넌트
 * 모든 마이크로서비스에서 공유하여 사용
 * 토큰 생성은 account-service에서만 담당
 */
@Component
class JwtValidator(
    private val jwtProperties: JwtProperties
) {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    fun validateToken(token: String): Boolean {
        parseClaims(token)
        return true
    }

    fun getAuthentication(token: String): Authentication {
        val claims = parseClaims(token)
        val id = claims.subject

        // 역할(role) 정보가 있다면 추출, 없으면 기본값 사용
        val role = claims.get("role", List::class.java)?.map {
            SimpleGrantedAuthority(it.toString())
        } ?: listOf(SimpleGrantedAuthority("CUSTOMER"))

        return UsernamePasswordAuthenticationToken(id, token, role)
    }

    private fun parseClaims(token: String): Claims {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: ExpiredJwtException) {
            throw TokenExpiredException()
        } catch (e: Exception) {
            throw TokenAuthenticationException()
        }
    }
}