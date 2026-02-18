package com.minimarket.accountservice.domain

import com.minimart.common.exception.DomainException

class AccountApiException(error: ErrorCode): DomainException(error.code, error.reason)

enum class ErrorCode(
    val code: String,
    val reason: String
) {
    EMAIL_DUPLICATED(
        "ACCOUNT-001",
        "이미 가입된 이메일입니다."
    ),

    EMAIL_NOT_FOUND(
        "ACCOUNT-002",
        "가입되지 않은 이메일입니다."
    ),

    INVALID_PASSWORD(
        "ACCOUNT-003",
        "비밀번호가 올바르지 않습니다."
    ),

    USER_NOT_FOUND(
        "ACCOUNT-004",
        "사용자를 찾을 수 없습니다."
    ),
}