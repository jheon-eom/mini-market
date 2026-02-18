package com.minimarket.accountservice.adapter.out.persistence.jpa

import com.minimarket.accountservice.adapter.out.persistence.jpa.entity.UserEntity
import com.minimarket.accountservice.application.port.out.UserWriter
import com.minimarket.accountservice.domain.User
import org.springframework.stereotype.Repository

@Repository
class UserJpaWriter(
    private val userRepository: UserRepository,
): UserWriter {
    override fun save(user: User): User {
        val userEntity = UserEntity.toEntity(user)
        val savedUserEntity = userRepository.save(userEntity)
        return UserEntity.toDomain(savedUserEntity)
    }
}
