package com.minimarket.accountservice.adapter.out.persistence.jpa

import com.minimarket.accountservice.adapter.out.persistence.entity.UserStatusHistoryEntity
import com.minimarket.accountservice.application.port.out.UserStatusHistoryWriter
import com.minimarket.accountservice.domain.UserId
import com.minimarket.accountservice.domain.UserStatusHistory
import com.minimarket.accountservice.domain.UserStatusHistoryId
import org.springframework.stereotype.Repository

@Repository
class UserStatusHistoryJpaWriter(
    private val userStatusHistoryRepository: UserStatusHistoryRepository
): UserStatusHistoryWriter {
    override fun save(userStatusHistory: UserStatusHistory): UserStatusHistory {
        return userStatusHistoryRepository.save(UserStatusHistoryEntity.toDomain(userStatusHistory))
            .let {
                UserStatusHistory(
                    id = UserStatusHistoryId(it.id!!),
                    userId = UserId(it.userId),
                    status = it.status,
                    reason = it.reason
                )
            }
    }
}