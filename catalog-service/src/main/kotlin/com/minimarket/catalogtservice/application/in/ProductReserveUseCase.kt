package com.minimarket.catalogtservice.application.`in`

import com.minimarket.catalogtservice.application.dto.ProductReserveCommand

interface ProductReserveUseCase {
    fun reserve(command: ProductReserveCommand)
}