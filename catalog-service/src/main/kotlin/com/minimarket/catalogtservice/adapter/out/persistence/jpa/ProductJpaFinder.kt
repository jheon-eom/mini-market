package com.minimarket.catalogtservice.adapter.out.persistence.jpa

import com.minimarket.catalogtservice.application.out.ProductFinder
import com.minimarket.catalogtservice.domain.Category
import com.minimarket.catalogtservice.domain.CategoryId
import com.minimarket.catalogtservice.domain.Price
import com.minimarket.catalogtservice.domain.Product
import com.minimarket.catalogtservice.domain.ProductApiException
import com.minimarket.catalogtservice.domain.ProductErrorCode.*
import com.minimarket.catalogtservice.domain.ProductId
import com.minimarket.catalogtservice.domain.ProductSearch
import com.minimarket.catalogtservice.domain.ProductSearchResult
import org.springframework.stereotype.Component

@Component
class ProductJpaFinder(
    private val productQueryDslRepository: ProductQueryDslRepository,
    private val productCategoryRepository: ProductCategoryRepository,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
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

    override fun findById(id: ProductId): Product = productRepository.findById(id.value)
        .orElseThrow { ProductApiException(NOT_FOUND) }
        .let {
            val productCategories = productCategoryRepository.findAllByProductId(it.id!!)
            val categories = categoryRepository.findAllByIdIn(
                productCategories.map { it.categoryId }
            )

            Product(
                id = ProductId(it.id!!),
                name = it.name,
                price = Price(original = it.originalPrice, current = it.currentPrice),
                stock = it.stock,
                categories = categories!!.map {
                    Category(id = CategoryId(it.id!!), name = it.name)
                }
            )
        }
}