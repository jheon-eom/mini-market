package com.minimarket.catalogtservice.application.`in`

import com.minimarket.catalogtservice.application.dto.ProductReserveCommand
import com.minimarket.catalogtservice.application.dto.ProductReserveResult

interface ProductReserveUseCase {
    fun reserve(command: ProductReserveCommand): ProductReserveResult
}