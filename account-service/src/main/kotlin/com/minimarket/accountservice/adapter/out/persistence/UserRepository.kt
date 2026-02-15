package com.minimarket.accountservice.adapter.out.persistence

import com.minimarket.accountservice.adapter.out.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity?
}