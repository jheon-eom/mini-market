package com.minimarket.catalogtservice.domain

class Category(
    val id: CategoryId? = null,

    var name: String
) {
    fun update(newName: String) {
        this.name = newName
    }
}