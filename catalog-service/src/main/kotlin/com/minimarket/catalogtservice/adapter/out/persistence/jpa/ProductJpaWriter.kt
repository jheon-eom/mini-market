package com.minimarket.catalogtservice.adapter.out.persistence.jpa

import com.minimarket.catalogtservice.adapter.out.persistence.jpa.entity.ProductCategoryEntity
import com.minimarket.catalogtservice.adapter.out.persistence.jpa.entity.ProductEntity
import com.minimarket.catalogtservice.application.out.ProductWriter
import com.minimarket.catalogtservice.domain.Product
import com.minimarket.catalogtservice.domain.ProductId
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductJpaWriter(
    private val productRepository: ProductRepository,
    private val productCategoryRepository: ProductCategoryRepository,
): ProductWriter {
    @Transactional
    override fun save(product: Product): Product {
        // productEntity 저장
        val savedProduct = productRepository.save(
            ProductEntity(
                id = product.id?.value,
                name = product.name,
                originalPrice = product.price.original,
                currentPrice = product.price.current,
                stock = product.stock
            )
        )

        // productCategoryEntity 저장
        for (category in product.categories) {
            productCategoryRepository.save(
                ProductCategoryEntity(
                    productId = savedProduct.id!!,
                    categoryId = category.id!!.value
                )
            )
        }

        return Product(
            id = ProductId(savedProduct.id!!),
            name = product.name,
            price = product.price,
            stock = product.stock,
            categories = product.categories
        )
    }
}