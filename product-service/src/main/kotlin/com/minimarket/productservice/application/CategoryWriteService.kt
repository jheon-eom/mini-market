package com.minimarket.productservice.application

import com.minimarket.productservice.application.dto.CategoryCreateCommand
import com.minimarket.productservice.application.dto.CategoryCreateResult
import com.minimarket.productservice.application.dto.CategoryUpdateCommand
import com.minimarket.productservice.application.dto.CategoryUpdateResult
import com.minimarket.productservice.application.`in`.CategoryUseCase
import com.minimarket.productservice.application.out.CategoryFinder
import com.minimarket.productservice.application.out.CategoryWriter
import com.minimarket.productservice.domain.Category
import com.minimarket.productservice.domain.CategoryApiException
import com.minimarket.productservice.domain.CategoryId
import com.minimarket.productservice.domain.ErrorCode.*
import org.springframework.stereotype.Service

@Service
class CategoryWriteService(
    private val categoryFinder: CategoryFinder,
    private val categoryWriter: CategoryWriter,
): CategoryUseCase {
    override fun create(command: CategoryCreateCommand): CategoryCreateResult {
        if (categoryFinder.findByName(command.name) != null) {
            throw CategoryApiException(NAME_DUPLICATED)
        }

        val saved = categoryWriter.save(Category(name = command.name))
        return CategoryCreateResult(id = saved.id!!.value)
    }

    override fun update(command: CategoryUpdateCommand): CategoryUpdateResult {
        val category = categoryFinder.findById(CategoryId(command.id))
            ?: throw CategoryApiException(NOT_FOUND)

        if (category.name == command.name) {
            return CategoryUpdateResult(id = category.id!!.value, updatedName = category.name)
        }

        category.update(command.name)
        val updated = categoryWriter.update(category)
        return CategoryUpdateResult(id = updated.id!!.value, updatedName = updated.name)
    }
}