package com.minimarket.catalogtservice.adapter.out.persistence.jpa

import com.minimarket.catalogtservice.adapter.out.persistence.jpa.entity.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository: JpaRepository<CategoryEntity, Long> {
    fun findByName(name: String): CategoryEntity?
}