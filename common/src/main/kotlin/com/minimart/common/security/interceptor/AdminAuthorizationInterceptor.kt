package com.minimart.common.security.interceptor

import com.minimart.common.exception.UnauthorizedException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

/**
 * @RequireAdmin 어노테이션이 붙은 메서드에 대해 ADMIN 권한을 체크하는 인터셉터
 */
@Component
class AdminAuthorizationInterceptor : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        // HandlerMethod가 아니면 통과 (정적 리소스 등)
        if (handler !is HandlerMethod) {
            return true
        }

        handler.getMethodAnnotation(RequireAdmin::class.java)
            ?: return true

        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw UnauthorizedException("인증되지 않은 사용자입니다.")

        val hasAdminRole = authentication.authorities
            ?.contains(SimpleGrantedAuthority("ADMIN")) ?: false

        if (!hasAdminRole) {
            throw UnauthorizedException("ADMIN 권한이 필요합니다.")
        }

        return true
    }
}