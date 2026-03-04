package com.minimarket.orderservice.adapter.out.persistence.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "order_status_history")
class OrderStatusHistoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    val orderId: String,

    @Column(nullable = false)
    val status: String,

    @Column(nullable = false)
    val reason: String,
): BaseEntity()