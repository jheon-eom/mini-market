package com.minimarket.catalogtservice.application.out

import com.minimarket.catalogtservice.domain.Category

interface CategoryWriter {
    fun save(category: Category): Category

    fun update(updateCategory: Category): Category
}
