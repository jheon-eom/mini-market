package com.minimarket.catalogtservice.domain

data class ProductSearchResult(
    val products: List<Product>,

    val nextProductIdKey: Long?,

    val hasNext: Boolean,
)
