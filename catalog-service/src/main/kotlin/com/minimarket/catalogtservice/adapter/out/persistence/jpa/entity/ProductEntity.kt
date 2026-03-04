package com.minimarket.catalogtservice.adapter.out.persistence.jpa.entity

import com.minimarket.catalogtservice.domain.Category
import com.minimarket.catalogtservice.domain.CategoryId
import com.minimarket.catalogtservice.domain.Price
import com.minimarket.catalogtservice.domain.Product
import com.minimarket.catalogtservice.domain.ProductId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction
import java.math.BigDecimal

@Entity
@Table(name = "product")
@SQLRestriction("is_deleted = false")
class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var originalPrice: BigDecimal,

    @Column(nullable = false)
    var currentPrice: BigDecimal,

    @Column(nullable = false)
    var stock: Int
) : BaseEntity() {
    fun toDomain(
        productCategoryMap: Map<Long, List<ProductCategoryEntity>>,
        categoryMap: Map<Long, CategoryEntity>
    ): Product {
        val productCategories = productCategoryMap[this.id] ?: emptyList()

        val categories = productCategories.mapNotNull {
            categoryMap[it.categoryId]?.let {
                Category(
                    id = CategoryId(it.id!!),
                    name = it.name
                )
            }
        }

        return Product(
            id = ProductId(this.id!!),
            name = this.name,
            price = Price.of(this.originalPrice, this.currentPrice),
            stock = this.stock,
            categories = categories
        )
    }
}