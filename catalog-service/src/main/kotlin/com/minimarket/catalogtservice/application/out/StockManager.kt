package com.minimarket.catalogtservice.application.out

interface StockManager {
    fun check(productId: Long): Boolean

    fun init(productId: Long, stock: Int)

    fun decrease(productId: Long, quantity: Int): Int

    fun add(productId: Long, quantity: Int)
}