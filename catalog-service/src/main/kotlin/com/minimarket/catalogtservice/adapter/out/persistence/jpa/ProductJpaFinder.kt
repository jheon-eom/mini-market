package com.minimarket.catalogtservice.adapter.out.persistence.jpa

import com.minimarket.catalogtservice.application.out.ProductFinder
import com.minimarket.catalogtservice.domain.ProductSearch
import com.minimarket.catalogtservice.domain.ProductSearchResult
import org.springframework.stereotype.Component

@Component
class ProductJpaFinder(
    private val productQueryDslRepository: ProductQueryDslRepository,
    private val productCategoryRepository: ProductCategoryRepository,
    private val categoryRepository: CategoryRepository
) : ProductFinder {
    override fun findAllBySearch(search: ProductSearch): ProductSearchResult {
        val products = productQueryDslRepository.findAllBySearch(search)

        val hasNext = products.size > search.size
        val resultProducts = if (hasNext) products.dropLast(1) else products
        val nextProductIdKey = if (hasNext) products[search.size - 1].id else null

        val productIds = resultProducts.mapNotNull { it.id }
        val productCategories = productCategoryRepository.findAllByProductIdIn(productIds)
        val productCategoryMap = productCategories.groupBy { it.productId }

        val categoryIds = productCategories.map { it.categoryId }.distinct()
        val categories = categoryRepository.findAllById(categoryIds).associateBy { it.id!! }

        return ProductSearchResult(
            products = resultProducts.map { it.toDomain(productCategoryMap, categories) },
            nextProductIdKey = nextProductIdKey,
            hasNext = hasNext
        )
    }
}