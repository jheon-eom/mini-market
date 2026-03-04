package com.minimarket.catalogtservice.adapter.out.persistence.jpa

import com.minimarket.catalogtservice.adapter.out.persistence.jpa.entity.ProductEntity
import com.minimarket.catalogtservice.domain.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository: JpaRepository<ProductEntity, Long> {

}