package com.minimarket.catalogtservice.application.dto

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

data class InventoryRollbackCommand(
    val eventId: String,
    val orderId: String
)