package com.minimarket.accountservice.domain

import com.minimarket.accountservice.domain.Errors.*
import com.minimart.common.exception.DomainException

// 도메인 영역에서 외부 모듈로의 의존성이 존재하는데 괜찮을까?
class EmailDuplicatedException: DomainException(EMAIL_DUPLICATED.code, EMAIL_DUPLICATED.reason)

enum class Errors(
    val code: String,
    val reason: String
) {
    EMAIL_DUPLICATED(
        "ACCOUNT-001",
        "Email is already in use."
    )
}