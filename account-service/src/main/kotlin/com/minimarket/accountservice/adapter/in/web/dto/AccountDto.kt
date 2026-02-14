package com.minimarket.accountservice.adapter.`in`.web.dto

import com.minimarket.accountservice.application.dto.JoinCommand
import com.minimarket.accountservice.application.dto.LoginCommand
import com.minimarket.accountservice.domain.UserRole

data class JoinRequest(
    val email: String,
    val password: String,
    val role: UserRole
) {
    fun toCommand(): JoinCommand {
        return JoinCommand(
            email = this.email,
            password = this.password,
            role = role
        )
    }
}

data class JoinResponse(
    val id: Long,
    val accessToken: String,
    val refreshToken: String
)

data class LoginRequest(
    val email: String,
    val password: String
) {
    fun toCommand(): LoginCommand {
        return LoginCommand(
            email = this.email,
            password = this.password
        )
    }
}