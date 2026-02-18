package com.minimarket.productservice.application.out

import com.minimarket.productservice.domain.Category

interface CategoryFinder {
    fun findByName(name: String): Category?
}