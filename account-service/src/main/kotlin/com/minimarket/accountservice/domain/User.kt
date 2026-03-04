package com.minimarket.accountservice.domain

class User(
    val id: UserId? = null,

    val email: Email,

    val role: UserRole,

    val passwordHash: String,

    var status: UserStatus = UserStatus.ACTIVE,
)