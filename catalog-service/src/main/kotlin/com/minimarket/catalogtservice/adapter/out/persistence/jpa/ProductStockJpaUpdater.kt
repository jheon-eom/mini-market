package com.minimarket.catalogtservice.adapter.out.persistence.jpa

import com.minimarket.catalogtservice.application.out.StockUpdater
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProductStockJpaUpdater(
    private val productRepository: ProductRepository
) : StockUpdater {

    @Transactional
    override fun updateStock(productId: Long, stock: Int) {
        productRepository.updateStock(productId, stock)
    }

    @Transactional
    override fun decreaseStock(productId: Long, quantity: Int) {
        productRepository.decreaseStock(productId, quantity)
    }

    @Transactional
    override fun updateStockBatch(stocks: Map<Long, Int>) {
        if (stocks.isEmpty()) return

        stocks.forEach { (productId, stock) ->
            productRepository.updateStock(productId, stock)
        }
    }
}