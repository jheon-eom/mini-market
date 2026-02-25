package com.minimarket.catalogtservice.application.out

/**
 * DB의 재고를 업데이트하는 Port
 */
interface StockUpdater {
    /**
     * 특정 상품의 재고를 설정된 값으로 업데이트
     */
    fun updateStock(productId: Long, stock: Int)

    /**
     * 특정 상품의 재고를 차감
     */
    fun decreaseStock(productId: Long, quantity: Int)

    /**
     * 여러 상품의 재고를 일괄 업데이트
     */
    fun updateStockBatch(stocks: Map<Long, Int>)
}