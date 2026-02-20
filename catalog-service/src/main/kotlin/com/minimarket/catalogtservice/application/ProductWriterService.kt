package com.minimarket.catalogtservice.application

import com.minimarket.catalogtservice.application.dto.ProductRegisterCommand
import com.minimarket.catalogtservice.application.dto.ProductRegisterResult
import com.minimarket.catalogtservice.application.`in`.ProductUseCase
import com.minimarket.catalogtservice.application.out.CategoryFinder
import com.minimarket.catalogtservice.application.out.ProductWriter
import com.minimarket.catalogtservice.domain.Category
import com.minimarket.catalogtservice.domain.Price
import com.minimarket.catalogtservice.domain.Product
import org.springframework.stereotype.Service

@Service
class ProductWriterService(
    private val categoryFinder: CategoryFinder,
    private val productWriter: ProductWriter
): ProductUseCase {
    override fun register(command: ProductRegisterCommand): ProductRegisterResult =
        Product(
            name = command.name,
            price = Price.of(
                original = command.originalPrice,
                current = command.currentPrice
            ),
            stock = command.stock,
            categories = categoryFinder.findAllByIds(command.categoryIds)
                .map { Category(it.id, it.name) }
        ).run {
            productWriter.save(this).run {
                ProductRegisterResult(id = id!!.value)
            }
        }
}