package com.minimarket.catalogtservice.application

import com.minimarket.catalogtservice.application.`in`.CategoryReader
import com.minimarket.catalogtservice.application.out.CategoryFinder
import com.minimarket.catalogtservice.domain.Category
import org.springframework.stereotype.Service

@Service
class CategoryReadService(
    private val categoryFinder: CategoryFinder
): CategoryReader {
    override fun getALl(): List<Category> {
        return categoryFinder.findAll()
    }
}