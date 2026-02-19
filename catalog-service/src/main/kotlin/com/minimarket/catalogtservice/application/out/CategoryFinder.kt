package com.minimarket.catalogtservice.application.out

import com.minimarket.catalogtservice.domain.Category
import com.minimarket.catalogtservice.domain.CategoryId

interface CategoryFinder {
    fun findByName(name: String): Category?

    fun findById(id: CategoryId): Category?

    fun findAll(): List<Category>
}