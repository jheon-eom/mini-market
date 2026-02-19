package com.minimarket.productservice.application

import com.minimarket.productservice.application.`in`.CategoryReader
import com.minimarket.productservice.application.out.CategoryFinder
import com.minimarket.productservice.domain.Category
import org.springframework.stereotype.Service

@Service
class CategoryReadService(
    private val categoryFinder: CategoryFinder
): CategoryReader {
    override fun getALl(): List<Category> {
        return categoryFinder.findAll()
    }
}