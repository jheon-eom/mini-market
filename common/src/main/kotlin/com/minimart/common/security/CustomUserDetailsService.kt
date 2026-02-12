package com.minimart.common.security

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

/**
 * Using generated security password: 747cc60f-73fa-4270-833c-29cfee3c6d0c
 * This generated password is for development use only. Your security configuration must be updated before running your application in production.
 *
 * 위의 메시지가 출력되지 않도록 하기 위한 UserDetailsService 구현체
 * 다른 방법이 없는지?
 */
@Component
class CustomUserDetailsService(): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return User(
            username,
            "",
            emptyList()
        )
    }
}