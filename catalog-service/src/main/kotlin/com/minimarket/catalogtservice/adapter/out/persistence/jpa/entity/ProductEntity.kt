package com.minimarket.catalogtservice.adapter.out.persistence.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction
import java.math.BigDecimal

@Entity
@Table(name = "product")
@SQLRestriction("is_deleted = false")
class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var originalPrice: BigDecimal,

    @Column(nullable = false)
    var currentPrice: BigDecimal,

    @Column(nullable = false)
    var stock: Int
) : BaseEntity()