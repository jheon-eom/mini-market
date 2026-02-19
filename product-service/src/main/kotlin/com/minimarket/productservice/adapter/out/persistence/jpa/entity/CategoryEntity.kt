package com.minimarket.productservice.adapter.out.persistence.jpa.entity

import jakarta.persistence.*
import org.hibernate.annotations.SQLRestriction

@Entity
@SQLRestriction("is_deleted = false")
class CategoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    var name: String
): BaseEntity() {
    fun update(newName: String) {
        this.name = newName
    }
}