package com.minimarket.paymentservice.domain

import com.minimart.common.exception.DomainException

class PaymentApiException(error: PaymentErrorCode): DomainException(error.code, error.reason)

enum class PaymentErrorCode(
    val code: String,
    val reason: String
) {
    INVALID_PAYMENT_AMOUNT(
        "PAYMENT_001",
        "결제 금액이 주문 금액과 일치하지 않습니다."
    ),

    NOT_FOUND(
        "PAYMENT_002",
        "결제 정보를 찾을 수 없습니다."
    ),
}