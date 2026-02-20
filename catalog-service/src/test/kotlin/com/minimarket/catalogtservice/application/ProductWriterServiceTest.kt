package com.minimarket.catalogtservice.application

import com.minimarket.catalogtservice.application.dto.CategoryCreateCommand
import com.minimarket.catalogtservice.application.dto.ProductRegisterCommand
import com.minimarket.catalogtservice.application.`in`.ProductUseCase
import com.minimarket.catalogtservice.application.out.CategoryFinder
import com.minimarket.catalogtservice.application.out.CategoryWriter
import com.minimarket.catalogtservice.application.out.ProductWriter
import com.minimarket.catalogtservice.domain.Category
import com.minimarket.catalogtservice.domain.CategoryApiException
import com.minimarket.catalogtservice.domain.CategoryId
import com.minimarket.catalogtservice.domain.Price
import com.minimarket.catalogtservice.domain.Product
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import java.math.BigDecimal

@DataJpaTest
class ProductWriterServiceTest {
    @Autowired private lateinit var categoryFinder: CategoryFinder
    @Autowired private lateinit var categoryWriter: CategoryWriter
    @Autowired private lateinit var productWriter: ProductWriter
    @Autowired private lateinit var entityManager: EntityManager

    private val productUseCase: ProductUseCase by lazy {
        ProductWriterService(
            categoryFinder = categoryFinder,
            productWriter = productWriter
        )
    }

    @Test
    fun `상품 등록 성공 테스트`() {
        // given
        val savedCategory = CategoryCreateCommand(
            name = "테스트 카테고리"
        ).run {
            categoryWriter.save(Category(name = this.name))
        }

        // when
        val result = ProductRegisterCommand(
            name = "테스트 상품",
            originalPrice = BigDecimal("10000"),
            currentPrice = BigDecimal("8000"),
            stock = 10,
            categoryIds = listOf(CategoryId(savedCategory.id!!.value))
        ).run {
            productUseCase.register(this)
        }

        // then
        assertNotNull(result.id)
    }

    @Test
    fun `존재하지 않는 카테고리로 상품 등록 시 실패`() {
        // given
        val nonExistentCategoryId = CategoryId(-1L)

        // when
        val command = ProductRegisterCommand(
            name = "테스트 상품",
            originalPrice = BigDecimal("10000"),
            currentPrice = BigDecimal("8000"),
            stock = 10,
            categoryIds = listOf(nonExistentCategoryId)
        )

        // then
        assertThrows<CategoryApiException> { productUseCase.register(command) }
    }
}