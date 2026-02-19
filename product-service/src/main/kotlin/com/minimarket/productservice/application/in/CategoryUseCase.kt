package com.minimarket.productservice.application.`in`

import com.minimarket.productservice.application.dto.CategoryCreateCommand
import com.minimarket.productservice.application.dto.CategoryCreateResult
import com.minimarket.productservice.application.dto.CategoryUpdateCommand
import com.minimarket.productservice.application.dto.CategoryUpdateResult

interface CategoryUseCase {
    fun create(command: CategoryCreateCommand): CategoryCreateResult

    fun update(command: CategoryUpdateCommand): CategoryUpdateResult
}