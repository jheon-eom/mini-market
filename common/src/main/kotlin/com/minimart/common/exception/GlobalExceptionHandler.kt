package com.minimart.common.exception

import com.minimart.common.dto.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException): ApiResponse {
        // TODO: MSA 환경에 맞는 로깅 전략 수립 필요 -> 분산 추적 시스템 연동
        // TODO: 어떤 도메인에서 어떠한 입력값으로 에러가 발생했는지 추적 가능해야 함
        logger.warn("Domain exception occurred", ex)

        return ApiResponse.fail(
            code = ex.code,
            reason = ex.reason
        )
    }
}