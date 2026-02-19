package com.minimarket.accountservice.adapter.out.persistence.jpa.entity

import com.minimarket.accountservice.domain.UserStatus
import com.minimarket.accountservice.domain.UserStatusHistory
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction

@Entity
@SQLRestriction("is_deleted = false and status = 'ACTIVE'")
class UserStatusHistoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id", nullable = false, updatable = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, updatable = false)
    val status: UserStatus,

    @Column(name = "reason", updatable = false)
    val reason: String?,
): BaseEntity() {
    companion object {
        fun toDomain(userStatusHistory: UserStatusHistory): UserStatusHistoryEntity {
            return UserStatusHistoryEntity(
                userId = userStatusHistory.userId.value,
                status = userStatusHistory.status,
                reason = userStatusHistory.reason
            )
        }
    }
}