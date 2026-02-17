package com.minimarket.accountservice.application.port.out

import com.minimarket.accountservice.domain.RefreshToken

interface RefreshTokenWriter {
    fun save(token: RefreshToken)
}