package com.minimarket.accountservice.adapter.`in`.web

import com.minimarket.accountservice.adapter.`in`.web.dto.JoinRequest
import com.minimarket.accountservice.adapter.`in`.web.dto.JoinResponse
import com.minimarket.accountservice.adapter.`in`.web.dto.LoginRequest
import com.minimarket.accountservice.application.dto.AuthToken
import com.minimarket.accountservice.application.port.`in`.AccountUseCase
import com.minimarket.accountservice.application.port.`in`.RefreshTokenUseCase
import com.minimarket.accountservice.domain.UserId
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/accounts")
class AccountController(
    private val accountUseCase: AccountUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
) {
    @PostMapping
    fun join(@RequestBody request: JoinRequest): ResponseEntity<JoinResponse> {
        val result = accountUseCase.join(request.toCommand())

        return ResponseEntity.status(HttpStatus.CREATED).body(
            JoinResponse(
                id = result.id,
                accessToken = result.accessToken,
                refreshToken = result.refreshToken
            )
        )
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthToken> {
        val result = accountUseCase.login(request.toCommand())

        return ResponseEntity.ok(
            AuthToken(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken
            )
        )
    }

    @PostMapping("/refresh")
    fun refresh(request: HttpServletRequest): ResponseEntity<AuthToken> {
        val refreshToken = request.getHeader("AuthorizationRefresh")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()

        val userId = request.getHeader("X-UserId")
            ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()

        val result = refreshTokenUseCase.refresh(refreshToken, UserId(userId.toLong()))

        return ResponseEntity.ok(
            AuthToken(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken
            )
        )
    }
}