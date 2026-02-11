package com.minimart.common.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "jwt")
@Component
data class JwtProperties(
    var secret: String = "",
    var accessTokenValidity: Long = 3600000,  // 1 hour
    var refreshTokenValidity: Long = 604800000  // 7 days
)