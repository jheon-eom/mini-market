package com.minimart.common.exception

class UnauthorizedException(
    override val message: String = "권한이 없습니다."
) : RuntimeException(message)