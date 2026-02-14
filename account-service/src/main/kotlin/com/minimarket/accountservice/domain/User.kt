package com.minimarket.accountservice.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "\"user\"")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 255)
    val email: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 255)
    val role: UserRole,

    @Column(name = "password_hash", nullable = false, length = 255)
    val passwordHash: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: UserStatus = UserStatus.ACTIVE,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)