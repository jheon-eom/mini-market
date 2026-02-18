package com.minimarket.productservice.adapter.`in`.web.dto

import com.minimarket.productservice.application.dto.CreateCategoryCommand

data class CategoryCreateRequest (
    val name: String
) {
    fun toCommand(): CreateCategoryCommand {
        return CreateCategoryCommand(name)
    }
}

data class CategoryCreateResponse (
    val id: Long,
)