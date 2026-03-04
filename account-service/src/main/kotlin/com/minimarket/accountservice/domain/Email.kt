package com.minimarket.accountservice.domain

data class Email(
    val value: String
) {
    init {
        require(value.isNotBlank()) { "이메일이 입력되지 않았습니다." }
        require(EMAIL_REGEX.matches(value)) { "올바른 이메일 형식이 아닙니다: $value" }
    }

    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    }
}
