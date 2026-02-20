package com.minimarket.catalogtservice.application

import com.minimarket.catalogtservice.application.`in`.ProductReader
import com.minimarket.catalogtservice.application.out.ProductFinder
import com.minimarket.catalogtservice.domain.ProductSearch
import com.minimarket.catalogtservice.domain.ProductSearchResult
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProductReadService(
    private val productFinder : ProductFinder,
): ProductReader {
    override fun search(search: ProductSearch): ProductSearchResult {
        return productFinder.findAllBySearch(search)
    }
}