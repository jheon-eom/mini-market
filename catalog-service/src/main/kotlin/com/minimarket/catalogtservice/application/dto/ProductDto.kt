package com.minimarket.catalogtservice.application.dto

import com.minimarket.catalogtservice.domain.CategoryId
import java.math.BigDecimal

data class ProductRegisterCommand(
    val name: String,
    val originalPrice: BigDecimal,
    val currentPrice: BigDecimal?,
    val stock: Int,
    val categoryIds: List<CategoryId>
) {
    init {
        require(name.isNotBlank()) { "상품 이름은 필수입니다." }
        require(originalPrice > BigDecimal.ZERO) { "상품 원가는 필수입니다." }
        require(stock > 0) { "상품 재고는 필수입니다." }
        require(!categoryIds.isEmpty()) { "상품 카테고리는 필수입니다." }
    }
}

data class ProductRegisterResult(
    val id: Long
)

data class ProductReserveCommand(
    val orderId: String,
    val items: List<ProductReserveItem>
)

data class ProductReserveItem(
    val productId: Long,
    val quantity: Int
)

data class ProductReserveResult(
    val success: Boolean,
    val message: String? = null
)