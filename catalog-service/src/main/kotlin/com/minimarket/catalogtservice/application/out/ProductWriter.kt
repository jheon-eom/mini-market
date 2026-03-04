package com.minimarket.catalogtservice.application.out

import com.minimarket.catalogtservice.domain.Product

interface ProductWriter {
    fun save(product: Product): Product
}