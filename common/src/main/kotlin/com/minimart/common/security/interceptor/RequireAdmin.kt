package com.minimart.common.security.interceptor

/**
 * ADMIN 권한이 필요한 메서드에 적용하는 어노테이션
 * 이 어노테이션이 붙은 메서드는 ADMIN 역할을 가진 사용자만 접근 가능
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequireAdmin