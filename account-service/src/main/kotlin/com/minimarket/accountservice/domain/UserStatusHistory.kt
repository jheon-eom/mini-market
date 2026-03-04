package com.minimarket.accountservice.domain

class UserStatusHistory(
    val id: UserStatusHistoryId? = null,

    val userId: UserId,

    val status: UserStatus,

    val reason: String? = "",
)