package com.minimarket.catalogtservice.adapter.out.persistence.jpa

import com.minimarket.catalogtservice.adapter.out.persistence.jpa.entity.ProductCategoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductCategoryRepository: JpaRepository<ProductCategoryEntity, Long> {
    fun findAllByProductIdIn(productIds: List<Long>): List<ProductCategoryEntity>

    fun findAllByProductId(id: Long): List<ProductCategoryEntity>
}