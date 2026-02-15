package com.minimarket.accountservice.application.port.out

import com.minimarket.accountservice.domain.User

interface UserWriter {
    fun save(user: User): User
}