package com.minimarket.catalogtservice.adapter.`in`.web

import com.minimarket.catalogtservice.adapter.`in`.web.dto.CategoryCreateRequest
import com.minimarket.catalogtservice.adapter.`in`.web.dto.CategoryCreateResponse
import com.minimarket.catalogtservice.adapter.`in`.web.dto.CategoryUpdateRequest
import com.minimarket.catalogtservice.adapter.`in`.web.dto.CategoryUpdateResponse
import com.minimarket.catalogtservice.application.`in`.CategoryReader
import com.minimarket.catalogtservice.application.`in`.CategoryUseCase
import com.minimarket.catalogtservice.domain.Category
import com.minimart.common.security.interceptor.RequireAdmin
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/categories")
class CategoryController(
    private val categoryUseCase: CategoryUseCase,
    private val categoryReader: CategoryReader,
) {
    @RequireAdmin
    @PostMapping
    fun create(@RequestBody request: CategoryCreateRequest)
    : ResponseEntity<CategoryCreateResponse> {
        val result = categoryUseCase.create(request.toCommand())
        return ResponseEntity.ok(CategoryCreateResponse(result.id))
    }

    @RequireAdmin
    @PutMapping
    fun update(@RequestBody request: CategoryUpdateRequest)
    : ResponseEntity<CategoryUpdateResponse> {
        val result = categoryUseCase.update(request.toCommand())
        return ResponseEntity.ok(CategoryUpdateResponse(result.id))
    }

    @GetMapping
    fun getAll(): ResponseEntity<List<Category>> {
        return ResponseEntity.ok(categoryReader.getALl())
    }
}