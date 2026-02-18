package com.minimarket.productservice.application.`in`

import com.minimarket.productservice.application.dto.CreateCategoryCommand
import com.minimarket.productservice.application.dto.CreateCategoryResult

interface CategoryUseCase {
    fun create(command: CreateCategoryCommand): CreateCategoryResult
}