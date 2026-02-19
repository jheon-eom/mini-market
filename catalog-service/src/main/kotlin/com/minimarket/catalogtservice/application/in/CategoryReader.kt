package com.minimarket.catalogtservice.application.`in`

import com.minimarket.catalogtservice.domain.Category

interface CategoryReader {
    fun getALl(): List<Category>
}