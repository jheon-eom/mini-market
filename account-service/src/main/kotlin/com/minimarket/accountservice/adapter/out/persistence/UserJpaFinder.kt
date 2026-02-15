package com.minimarket.accountservice.adapter.out.persistence

import com.minimarket.accountservice.adapter.out.persistence.entity.UserEntity
import com.minimarket.accountservice.application.port.out.UserFinder
import com.minimarket.accountservice.domain.User
import org.springframework.stereotype.Repository

@Repository
class UserJpaFinder(
    private val userRepository: UserRepository,
): UserFinder {
    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
            ?.let { UserEntity.toDomain(it) }
    }
}