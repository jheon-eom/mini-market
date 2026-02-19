package com.minimarket.productservice.domain

class Product(
    val id: ProductId,

    val name: String,

    val price: Price,

    val stock: Int,
)