package com.minimarket.catalogtservice.application.out

import com.minimarket.catalogtservice.domain.ProductSearch
import com.minimarket.catalogtservice.domain.ProductSearchResult

interface ProductFinder {
    fun findAllBySearch(search: ProductSearch): ProductSearchResult
}