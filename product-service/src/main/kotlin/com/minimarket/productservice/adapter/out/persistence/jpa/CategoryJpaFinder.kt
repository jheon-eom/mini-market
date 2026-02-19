package com.minimarket.productservice.adapter.out.persistence.jpa

import com.minimarket.productservice.application.out.CategoryFinder
import com.minimarket.productservice.domain.Category
import com.minimarket.productservice.domain.CategoryId
import org.springframework.stereotype.Component

@Component
class CategoryJpaFinder(
    private val categoryRepository: CategoryRepository
): CategoryFinder {
    override fun findByName(name: String): Category? {
        return categoryRepository.findByName(name)
            ?.let { Category(id = CategoryId(it.id!!), name = it.name) }
    }

    override fun findById(id: CategoryId): Category? {
        return categoryRepository.findById(id.value)
            .orElse(null)
            ?.let { Category(id = CategoryId(it.id!!), name = it.name) }
    }

    override fun findAll(): List<Category> = categoryRepository.findAll()
        .map { Category(id = CategoryId(it.id!!), name = it.name) }
}