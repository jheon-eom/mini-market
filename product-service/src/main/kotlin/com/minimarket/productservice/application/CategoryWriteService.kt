package com.minimarket.productservice.application

import com.minimarket.productservice.application.dto.CreateCategoryCommand
import com.minimarket.productservice.application.dto.CreateCategoryResult
import com.minimarket.productservice.application.`in`.CategoryUseCase
import com.minimarket.productservice.application.out.CategoryFinder
import com.minimarket.productservice.application.out.CategoryWriter
import com.minimarket.productservice.domain.Category
import com.minimarket.productservice.domain.CategoryApiException
import com.minimarket.productservice.domain.ErrorCode
import com.minimarket.productservice.domain.ErrorCode.NAME_DUPLICATED
import org.springframework.stereotype.Service

@Service
class CategoryWriteService(
    private val categoryFinder: CategoryFinder,
    private val categoryWriter: CategoryWriter,
): CategoryUseCase {
    override fun create(command: CreateCategoryCommand): CreateCategoryResult {
        if (categoryFinder.findByName(command.name) != null) {
            throw CategoryApiException(NAME_DUPLICATED)
        }

        val saved = categoryWriter.save(Category(name = command.name))
        return CreateCategoryResult(id = saved.id!!.value)
    }
}