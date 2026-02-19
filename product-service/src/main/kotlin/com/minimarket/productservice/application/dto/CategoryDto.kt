package com.minimarket.productservice.application.dto

data class CategoryCreateCommand(
    val name: String
)

data class CategoryCreateResult(
    val id: Long,
)

data class CategoryUpdateCommand(
    val id: Long,
    val name: String
)

data class CategoryUpdateResult(
    val id: Long,
    val updatedName: String
)