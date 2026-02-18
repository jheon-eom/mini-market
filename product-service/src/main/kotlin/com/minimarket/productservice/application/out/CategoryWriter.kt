package com.minimarket.productservice.application.out

import com.minimarket.productservice.domain.Category

interface CategoryWriter {
    fun save(category: Category): Category
}
