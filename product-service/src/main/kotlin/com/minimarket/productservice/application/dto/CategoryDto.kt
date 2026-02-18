package com.minimarket.productservice.application.dto

data class CreateCategoryCommand(
    val name: String
)

data class CreateCategoryResult(
    val id: Long,
)