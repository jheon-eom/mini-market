package com.minimarket.orderservice.adapter.out.persistence.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "shipping_info")
class ShippingInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true, updatable = false)
    val orderId: String,

    @Column(nullable = false)
    val receiverName: String,

    @Column(nullable = false)
    var address: String,

    @Column(nullable = false)
    var detailAddress: String
): BaseEntity()