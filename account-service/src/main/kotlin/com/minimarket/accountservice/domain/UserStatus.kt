package com.minimarket.accountservice.domain

enum class UserStatus(val description: String) {
    ACTIVE("활성"),
    DEACTIVATED("비활성"),
    SUSPENDED("정지"),
}