package com.minimarket.catalogtservice.domain

class Product(
    val id: ProductId? = null,

    var name: String,

    val price: Price,

    val stock: Int,

    val categories: List<Category>
)