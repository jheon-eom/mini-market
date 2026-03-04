package com.minimarket.catalogtservice.application.`in`

import com.minimarket.catalogtservice.application.dto.ProductRegisterCommand
import com.minimarket.catalogtservice.application.dto.ProductRegisterResult

interface ProductUseCase {
    fun register(command: ProductRegisterCommand): ProductRegisterResult
}