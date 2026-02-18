package com.minimarket.productservice.adapter.out.persistence.jpa

import com.minimarket.productservice.adapter.out.persistence.jpa.entity.CategoryEntity
import com.minimarket.productservice.application.out.CategoryWriter
import com.minimarket.productservice.domain.Category
import com.minimarket.productservice.domain.CategoryId
import org.springframework.stereotype.Component

@Component
class CategoryJpaWriter(
    private val categoryRepository: CategoryRepository
): CategoryWriter {
    override fun save(category: Category): Category =
        categoryRepository.save(CategoryEntity(name = category.name))
            .let { Category(id = CategoryId(it.id!!), name = it.name) }
}