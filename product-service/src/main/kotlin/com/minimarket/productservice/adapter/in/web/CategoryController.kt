package com.minimarket.productservice.adapter.`in`.web

import com.minimarket.productservice.adapter.`in`.web.dto.CategoryCreateRequest
import com.minimarket.productservice.adapter.`in`.web.dto.CategoryCreateResponse
import com.minimarket.productservice.application.`in`.CategoryUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/categories")
class CategoryController(
    private val categoryUserCase: CategoryUseCase
) {
    @PostMapping
    fun create(request: CategoryCreateRequest)
    : ResponseEntity<CategoryCreateResponse> {
        val category = categoryUserCase.create(request.toCommand())
        return ResponseEntity.ok(CategoryCreateResponse(category.id))
    }
}