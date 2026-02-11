package com.minimarket.accountservice.application.port.out

import com.minimarket.accountservice.application.dto.AuthToken

interface AuthProvider {

    fun generate(customerId: Long, email: String): AuthToken
}