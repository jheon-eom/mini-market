package com.minimarket.accountservice.domain

class User(
    val id: Long? = null,

    val email: String,

    val role: UserRole,

    val passwordHash: String,

    var status: UserStatus = UserStatus.ACTIVE,
)