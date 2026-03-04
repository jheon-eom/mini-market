package com.minimarket.catalogtservice.application.out

import com.minimarket.catalogtservice.domain.Product
import com.minimarket.catalogtservice.domain.ProductId
import com.minimarket.catalogtservice.domain.ProductSearch
import com.minimarket.catalogtservice.domain.ProductSearchResult

interface ProductFinder {
    fun findAllBySearch(search: ProductSearch): ProductSearchResult

    fun findById(id: ProductId): Product

    fun findStockById(productId: Long): Int

    fun findAllByOrderId(orderId: String): List<Product>
}