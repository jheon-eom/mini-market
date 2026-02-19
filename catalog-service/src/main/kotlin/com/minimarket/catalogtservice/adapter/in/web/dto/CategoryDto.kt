package com.minimarket.catalogtservice.adapter.`in`.web.dto

import com.minimarket.catalogtservice.application.dto.CategoryUpdateCommand
import com.minimarket.catalogtservice.application.dto.CategoryCreateCommand

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