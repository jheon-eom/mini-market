package com.minimarket.catalogtservice.domain

import com.minimart.common.exception.DomainException

class ProductApiException(error: ProductErrorCode): DomainException(error.code, error.reason)

enum class ProductErrorCode(
    val code: String,
    val reason: String
) {
    NAME_DUPLICATED(
        "PRODUCT-001",
        "이미 존재하는 상품명입니다."
    ),

    NOT_FOUND(
        "PRODUCT-002",
        "존재하지 않는 상품입니다."
    )
}