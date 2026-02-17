package com.minimarket.accountservice.adapter.out.persistence.jpa

import com.minimarket.accountservice.adapter.out.persistence.entity.UserStatusHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserStatusHistoryRepository: JpaRepository<UserStatusHistoryEntity, Long> {
}