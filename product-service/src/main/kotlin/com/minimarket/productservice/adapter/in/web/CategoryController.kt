package com.minimarket.productservice.adapter.`in`.web

import com.minimarket.productservice.adapter.`in`.web.dto.CategoryCreateRequest
import com.minimarket.productservice.adapter.`in`.web.dto.CategoryCreateResponse
import com.minimarket.productservice.adapter.`in`.web.dto.CategoryUpdateRequest
import com.minimarket.productservice.adapter.`in`.web.dto.CategoryUpdateResponse
import com.minimarket.productservice.application.`in`.CategoryReader
import com.minimarket.productservice.application.`in`.CategoryUseCase
import com.minimarket.productservice.domain.Category
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/categories")
class CategoryController(
    private val categoryUseCase: CategoryUseCase,
    private val categoryReader: CategoryReader,
) {
    @PostMapping
    fun create(request: CategoryCreateRequest): ResponseEntity<CategoryCreateResponse> {
        val result = categoryUseCase.create(request.toCommand())
        return ResponseEntity.ok(CategoryCreateResponse(result.id))
    }

    @PutMapping
    fun update(request: CategoryUpdateRequest): ResponseEntity<CategoryUpdateResponse> {
        val result = categoryUseCase.update(request.toCommand())
        return ResponseEntity.ok(CategoryUpdateResponse(result.id))
    }

    @GetMapping
    fun getAll(): ResponseEntity<List<Category>> {
        return ResponseEntity.ok(categoryReader.getALl())
    }
}