package com.minimarket.productservice.adapter.`in`.web.dto

import com.minimarket.productservice.application.dto.CategoryUpdateCommand
import com.minimarket.productservice.application.dto.CategoryCreateCommand

data class CategoryCreateRequest (
    val name: String
) {
    fun toCommand(): CategoryCreateCommand {
        return CategoryCreateCommand(name)
    }
}

data class CategoryCreateResponse (
    val id: Long,
)

data class CategoryUpdateRequest (
    val id: Long,
    val name: String
) {
    fun toCommand(): CategoryUpdateCommand {
        return CategoryUpdateCommand(id = id, name = name)
    }
}

data class CategoryUpdateResponse (
    val id: Long,
)