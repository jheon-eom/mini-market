package com.minimarket.catalogtservice.application.`in`

import com.minimarket.catalogtservice.domain.ProductSearch
import com.minimarket.catalogtservice.domain.ProductSearchResult

interface ProductReader {
    fun search(search: ProductSearch): ProductSearchResult
}