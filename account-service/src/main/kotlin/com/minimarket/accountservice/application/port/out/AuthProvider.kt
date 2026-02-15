package com.minimarket.accountservice.application.port.out

import com.minimarket.accountservice.application.dto.AuthToken
import com.minimarket.accountservice.domain.User

interface AuthProvider {
    fun generate(user: User): AuthToken
}