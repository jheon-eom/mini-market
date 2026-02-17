package com.minimarket.accountservice.adapter.out.persistence.entity

import com.minimarket.accountservice.domain.Email
import com.minimarket.accountservice.domain.User
import com.minimarket.accountservice.domain.UserId
import com.minimarket.accountservice.domain.UserRole
import com.minimarket.accountservice.domain.UserStatus
import jakarta.persistence.*

@Entity
@Table(name = "\"user\"")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 255)
    val email: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 255)
    val role: UserRole,

    @Column(name = "password_hash", nullable = false, length = 255)
    var passwordHash: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: UserStatus = UserStatus.ACTIVE,
): BaseEntity() {
    companion object {
        fun toEntity(user: User): UserEntity {
            return UserEntity(
                email = user.email.value,
                role = user.role,
                passwordHash = user.passwordHash,
                status = user.status,
            )
        }

        fun toDomain(userEntity: UserEntity): User {
            return User(
                id = UserId(userEntity.id!!),
                email = Email(userEntity.email),
                role = userEntity.role,
                passwordHash = userEntity.passwordHash,
                status = userEntity.status,
            )
        }
    }
}