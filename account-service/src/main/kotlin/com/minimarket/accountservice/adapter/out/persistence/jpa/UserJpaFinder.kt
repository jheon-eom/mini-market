package com.minimarket.accountservice.adapter.out.persistence.jpa

import com.minimarket.accountservice.adapter.out.persistence.jpa.entity.UserEntity
import com.minimarket.accountservice.application.port.out.UserFinder
import com.minimarket.accountservice.domain.User
import com.minimarket.accountservice.domain.UserId
import org.springframework.stereotype.Repository

@Repository
class UserJpaFinder(
    private val userRepository: UserRepository,
): UserFinder {
    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
            ?.let { UserEntity.toDomain(it) }
    }

    override fun findById(userId: UserId): User {
        return userRepository.findById(userId.value)
            .orElse(null)
            .let { UserEntity.toDomain(it) }
    }
}