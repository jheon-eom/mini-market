package com.minimarket.catalogtservice.adapter.`in`.web.dto

import com.minimarket.catalogtservice.application.dto.ProductRegisterCommand
import com.minimarket.catalogtservice.domain.CategoryId
import com.minimarket.catalogtservice.domain.ProductSearch
import java.math.BigDecimal

data class ProductRegisterRequest(
    val name: String,
    val originalPrice: BigDecimal,
    val currentPrice: BigDecimal?,
    val stock: Int,
    val categoryIds: List<Long>
) {
    fun toCommand(): ProductRegisterCommand {
        return ProductRegisterCommand(
            name = name,
            originalPrice = originalPrice,
            currentPrice = currentPrice,
            stock = stock,
            categoryIds = categoryIds.map { CategoryId(it) }
        )
    }
}

data class ProductRegisterResponse(
    val id: Long,
)

data class ProductSearchRequest(
    val categoryIds: List<Long>? = null,
    val keyword: String? = null,
    val productIdKey: Long? = null,
    val size: Int = 10
) {
    fun toSearch(): ProductSearch {
        return ProductSearch(
            categoryIds = categoryIds?.map { CategoryId(it) },
            keyword = keyword,
            productIdKey = productIdKey,
            size = size
        )
    }
}