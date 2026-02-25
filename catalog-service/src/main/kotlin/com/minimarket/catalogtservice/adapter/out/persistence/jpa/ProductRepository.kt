package com.minimarket.catalogtservice.adapter.out.persistence.jpa

import com.minimarket.catalogtservice.adapter.out.persistence.jpa.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ProductRepository: JpaRepository<ProductEntity, Long> {

    @Modifying
    @Query("UPDATE ProductEntity p SET p.stock = :stock WHERE p.id = :productId")
    fun updateStock(productId: Long, stock: Int): Int

    @Modifying
    @Query("UPDATE ProductEntity p SET p.stock = p.stock - :quantity WHERE p.id = :productId")
    fun decreaseStock(productId: Long, quantity: Int): Int
}