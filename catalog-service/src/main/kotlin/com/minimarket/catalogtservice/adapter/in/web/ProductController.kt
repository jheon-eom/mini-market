package com.minimarket.catalogtservice.adapter.`in`.web

import com.minimarket.catalogtservice.adapter.`in`.web.dto.ProductRegisterRequest
import com.minimarket.catalogtservice.adapter.`in`.web.dto.ProductRegisterResponse
import com.minimarket.catalogtservice.application.`in`.ProductUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val productUseCase: ProductUseCase
) {
    @PostMapping
    fun register(@RequestBody request: ProductRegisterRequest)
    : ResponseEntity<ProductRegisterResponse> {
        val result = productUseCase.register(request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ProductRegisterResponse(id = result.id))
    }
}