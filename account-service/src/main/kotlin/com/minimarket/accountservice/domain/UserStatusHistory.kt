package com.minimarket.accountservice.domain

class UserStatusHistory(
    val id: Long? = null,

    val userId: Long,

    val status: UserStatus,

    val reason: String?,
)