package com.minimarket.productservice.application.out

import com.minimarket.productservice.domain.Category
import com.minimarket.productservice.domain.CategoryId

interface CategoryFinder {
    fun findByName(name: String): Category?

    fun findById(id: CategoryId): Category?

    fun findAll(): List<Category>
}