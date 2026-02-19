package com.minimarket.catalogtservice.domain

import com.minimart.common.exception.DomainException

class CategoryApiException(error: ErrorCode): DomainException(error.code, error.reason)

enum class ErrorCode(
    val code: String,
    val reason: String
) {
    NAME_DUPLICATED(
        "PRODUCT-001",
        "이미 존재하는 카테고리명입니다."
    ),

    NOT_FOUND(
        "PRODUCT-002",
        "존재하지 않는 카테고리입니다."
    )
}