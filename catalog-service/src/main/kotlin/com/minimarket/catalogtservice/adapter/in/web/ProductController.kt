package com.minimarket.catalogtservice.adapter.`in`.web

import com.minimarket.catalogtservice.adapter.`in`.web.dto.ProductRegisterRequest
import com.minimarket.catalogtservice.adapter.`in`.web.dto.ProductRegisterResponse
import com.minimarket.catalogtservice.adapter.`in`.web.dto.ProductSearchRequest
import com.minimarket.catalogtservice.application.`in`.ProductReader
import com.minimarket.catalogtservice.application.`in`.ProductUseCase
import com.minimarket.catalogtservice.domain.ProductSearchResult
import com.minimart.common.security.interceptor.RequireAdmin
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val productUseCase: ProductUseCase,
    private val productReader: ProductReader
) {
    @RequireAdmin
    @PostMapping
    fun register(@RequestBody request: ProductRegisterRequest)
    : ResponseEntity<ProductRegisterResponse> {
        val result = productUseCase.register(request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ProductRegisterResponse(id = result.id))
    }

    @GetMapping
    fun search(request: ProductSearchRequest): ResponseEntity<ProductSearchResult> {
        return ResponseEntity.ok().body(productReader.search(request.toSearch()))
    }
}