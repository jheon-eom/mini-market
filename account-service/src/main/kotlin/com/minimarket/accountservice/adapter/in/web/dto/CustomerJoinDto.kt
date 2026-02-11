package com.minimarket.accountservice.adapter.`in`.web.dto

import com.minimarket.accountservice.application.dto.CustomerJoinCommand

data class CustomerJoinRequest(
    val email: String,
    val password: String
) {
    fun toCommand(): CustomerJoinCommand {
        return CustomerJoinCommand(
            email = this.email,
            password = this.password
        )
    }
}

data class CustomerJoinResponse(
    val id: Long,
    val accessToken: String,
    val refreshToken: String
)