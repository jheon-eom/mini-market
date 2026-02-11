package com.minimart.common.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse(
    val success: Boolean,
    val data: Any? = null,
    val error: Error? = null,
) {
    companion object {
        fun success(data: Any? = null): ApiResponse {
            return ApiResponse(
                success = true,
                data = data
            )
        }

        fun fail(code: String? = null, reason: String? = null): ApiResponse {
            return ApiResponse(
                success = false,
                error = Error(
                    code = code,
                    reason = reason
                )
            )
        }
    }
}

data class Error(
    val code: String? = null,
    val reason: String? = null,
)