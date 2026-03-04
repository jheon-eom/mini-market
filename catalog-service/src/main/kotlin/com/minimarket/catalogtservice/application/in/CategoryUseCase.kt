package com.minimarket.catalogtservice.application.`in`

import com.minimarket.catalogtservice.application.dto.CategoryCreateCommand
import com.minimarket.catalogtservice.application.dto.CategoryCreateResult
import com.minimarket.catalogtservice.application.dto.CategoryUpdateCommand
import com.minimarket.catalogtservice.application.dto.CategoryUpdateResult

interface CategoryUseCase {
    fun create(command: CategoryCreateCommand): CategoryCreateResult

    fun update(command: CategoryUpdateCommand): CategoryUpdateResult
}