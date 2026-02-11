package com.minimart.common.exception

abstract class DomainException(
    val code: String,
    val reason: String,
): RuntimeException()