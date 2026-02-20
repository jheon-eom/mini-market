package com.minimarket.catalogtservice.domain

data class ProductSearch(
    val categoryIds: List<CategoryId>?,
    val keyword: String?,
    val productIdKey: Long?,
    val size: Int
) {
    fun plusOneSize(): Long {
        return size + 1L
    }
}
