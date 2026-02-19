package com.minimarket.productservice.application.`in`

import com.minimarket.productservice.domain.Category

interface CategoryReader {
    fun getALl(): List<Category>
}