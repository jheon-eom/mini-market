package com.minimarket.catalogtservice.application.out

interface StockManager {
    fun check(productId: Long): Boolean

    fun init(productId: Long, stock: Int)

    fun decrease(productId: Long, quantity: Int): Int

    fun add(productId: Long, quantity: Int)

    /**
     * Redis에 저장된 모든 상품의 재고를 조회
     * @return Map<ProductId, Stock>
     */
    fun getAllStocks(): Map<Long, Int>

    /**
     * 특정 상품의 재고를 조회
     */
    fun getStock(productId: Long): Int?
}