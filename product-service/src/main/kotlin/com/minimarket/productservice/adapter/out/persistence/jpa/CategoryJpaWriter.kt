package com.minimarket.productservice.adapter.out.persistence.jpa

import com.minimarket.productservice.adapter.out.persistence.jpa.entity.CategoryEntity
import com.minimarket.productservice.application.out.CategoryWriter
import com.minimarket.productservice.domain.Category
import com.minimarket.productservice.domain.CategoryApiException
import com.minimarket.productservice.domain.CategoryId
import com.minimarket.productservice.domain.ErrorCode.*
import org.springframework.stereotype.Component

@Component
class CategoryJpaWriter(
    private val categoryRepository: CategoryRepository
): CategoryWriter {
    override fun save(category: Category): Category =
        categoryRepository.save(CategoryEntity(name = category.name))
            .let { Category(id = CategoryId(it.id!!), name = it.name) }

    override fun update(updateCategory: Category): Category {
        val category = categoryRepository.findById(updateCategory.id!!.value)
            .orElseThrow { CategoryApiException(NOT_FOUND) }

        category.name = updateCategory.name

        return categoryRepository.save(category)
            .let { Category(id = CategoryId(it.id!!), name = it.name) }
    }
}