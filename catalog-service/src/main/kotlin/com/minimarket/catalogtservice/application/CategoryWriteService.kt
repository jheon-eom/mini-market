package com.minimarket.catalogtservice.application

import com.minimarket.catalogtservice.application.dto.CategoryCreateCommand
import com.minimarket.catalogtservice.application.dto.CategoryCreateResult
import com.minimarket.catalogtservice.application.dto.CategoryUpdateCommand
import com.minimarket.catalogtservice.application.dto.CategoryUpdateResult
import com.minimarket.catalogtservice.application.`in`.CategoryUseCase
import com.minimarket.catalogtservice.application.out.CategoryFinder
import com.minimarket.catalogtservice.application.out.CategoryWriter
import com.minimarket.catalogtservice.domain.Category
import com.minimarket.catalogtservice.domain.CategoryApiException
import com.minimarket.catalogtservice.domain.CategoryId
import com.minimarket.catalogtservice.domain.CategoryErrorCode.*
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