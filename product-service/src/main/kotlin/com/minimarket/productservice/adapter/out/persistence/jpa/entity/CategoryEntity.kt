package com.minimarket.productservice.adapter.out.persistence.jpa.entity

import jakarta.persistence.*

@Entity
class CategoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val name: String
): BaseEntity()