package com.minimarket.catalogtservice.adapter.out.persistence.jpa

import com.minimarket.catalogtservice.adapter.out.persistence.jpa.entity.ProductEntity
import com.minimarket.catalogtservice.adapter.out.persistence.jpa.entity.QProductCategoryEntity
import com.minimarket.catalogtservice.adapter.out.persistence.jpa.entity.QProductEntity
import com.minimarket.catalogtservice.domain.CategoryId
import com.minimarket.catalogtservice.domain.ProductSearch
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ProductQueryDslRepository(
    private val queryFactory: JPAQueryFactory
) {
    private val product = QProductEntity.productEntity
    private val productCategory = QProductCategoryEntity.productCategoryEntity

    fun findAllBySearch(search: ProductSearch): List<ProductEntity> {
        return queryFactory
            .selectFrom(product)
            .where(
                categoryIdsIn(search.categoryIds),
                keywordContains(search.keyword),
                productIdLessThan(search.productIdKey)
            )
            .orderBy(product.id.desc())
            .limit(search.size + 1L)
            .fetch()
    }

    private fun categoryIdsIn(categoryIds: List<CategoryId>?): BooleanExpression? {
        if (categoryIds.isNullOrEmpty()) {
            return null
        }

        val productIds = queryFactory
            .select(productCategory.productId)
            .from(productCategory)
            .where(productCategory.categoryId.`in`(categoryIds.map { it.value }))
            .fetch()

        return if (productIds.isEmpty()) {
            product.id.eq(-1L) // 결과가 없도록
        } else {
            product.id.`in`(productIds)
        }
    }

    private fun keywordContains(keyword: String?): BooleanExpression? {
        return keyword?.let { product.name.contains(it) }
    }

    private fun productIdLessThan(productIdKey: Long?): BooleanExpression? {
        return productIdKey?.let { product.id.lt(it) }
    }
}