package com.minimarket.accountservice.application.port.out

import com.minimarket.accountservice.domain.User
import com.minimarket.accountservice.domain.UserId

interface UserFinder {
    fun findByEmail(email: String): User?
    fun findById(userId: UserId): User?
}