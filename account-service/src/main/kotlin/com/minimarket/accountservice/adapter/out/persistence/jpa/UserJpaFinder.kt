package com.minimarket.accountservice.adapter.out.persistence.jpa

import com.minimarket.accountservice.adapter.out.persistence.entity.UserEntity
import com.minimarket.accountservice.application.port.out.UserFinder
import com.minimarket.accountservice.domain.AccountApiException
import com.minimarket.accountservice.domain.ErrorCode
import com.minimarket.accountservice.domain.ErrorCode.USER_NOT_FOUND
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
            .orElseThrow { AccountApiException(USER_NOT_FOUND) }
            .let { UserEntity.toDomain(it) }
    }
}