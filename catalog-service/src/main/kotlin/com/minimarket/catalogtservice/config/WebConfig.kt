package com.minimarket.catalogtservice.config

import com.minimart.common.security.interceptor.AdminAuthorizationInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val adminAuthorizationInterceptor: AdminAuthorizationInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(adminAuthorizationInterceptor)
            .addPathPatterns("/api/**")
    }
}