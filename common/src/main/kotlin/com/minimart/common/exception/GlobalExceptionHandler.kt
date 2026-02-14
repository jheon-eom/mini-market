package com.minimart.common.exception

import com.minimart.common.dto.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException): ResponseEntity<ApiResponse> {
        // TODO: MSA 환경에 맞는 로깅 전략 수립 필요 -> 분산 추적 시스템 연동
        // TODO: 어떤 도메인에서 어떠한 입력값으로 에러가 발생했는지 추적 가능해야 함
        logger.warn("Domain exception occurred", ex)

        return ResponseEntity.badRequest().body(
            ApiResponse.fail(
                code = ex.code,
                reason = ex.reason
            )
        )
    }

    @ExceptionHandler(TokenExpiredException::class)
    fun handleTokenExpiredException(ex: Exception): ResponseEntity<ApiResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ApiResponse.fail(
                code = "ACCESS_TOKEN_EXPIRED",
                reason = ex.message
            )
        )
    }

    @ExceptionHandler(TokenAuthenticationException::class)
    fun handleTokenAuthenticationException(ex: Exception): ResponseEntity<ApiResponse> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ApiResponse.fail(
                code = "TOKEN_AUTHENTICATION_FAILED",
                reason = ex.message
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun exception(ex: Exception): ResponseEntity<ApiResponse> {
        logger.warn("Exception occurred", ex)

        return ResponseEntity.status(500).body(
            ApiResponse.fail(
                code = "INTERNAL_SERVER_ERROR",
                reason = ex.message
            )
        )
    }
}