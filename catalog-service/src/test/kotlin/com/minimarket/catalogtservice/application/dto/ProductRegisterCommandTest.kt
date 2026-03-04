package com.minimarket.catalogtservice.application.dto

import com.minimarket.catalogtservice.domain.CategoryId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class ProductRegisterCommandTest {
    @Test
    fun `상품 등록 커맨드 생성 성공`() {
        // given
        val name = "테스트 상품"
        val originalPrice = BigDecimal("10000")
        val currentPrice = BigDecimal("9000")
        val stock = 100
        val categoryIds = listOf(CategoryId(1L))

        // when
        val command = ProductRegisterCommand(
            name = name,
            originalPrice = originalPrice,
            currentPrice = currentPrice,
            stock = stock,
            categoryIds = categoryIds
        )

        // then
        assertEquals(name, command.name)
        assertEquals(originalPrice, command.originalPrice)
        assertEquals(currentPrice, command.currentPrice)
        assertEquals(stock, command.stock)
        assertEquals(categoryIds, command.categoryIds)
    }

    @Test
    fun `상품 이름이 공백이면 예외 발생`() {
        // given
        val name = "   "
        val originalPrice = BigDecimal("10000")
        val stock = 100
        val categoryIds = listOf(CategoryId(1L))

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            ProductRegisterCommand(
                name = name,
                originalPrice = originalPrice,
                currentPrice = null,
                stock = stock,
                categoryIds = categoryIds
            )
        }

        assertEquals("상품 이름은 필수입니다.", exception.message)
    }

    @Test
    fun `상품 원가가 0 이하면 예외 발생`() {
        // given
        val name = "테스트 상품"
        val originalPrice = BigDecimal.ZERO
        val stock = 100
        val categoryIds = listOf(CategoryId(1L))

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            ProductRegisterCommand(
                name = name,
                originalPrice = originalPrice,
                currentPrice = null,
                stock = stock,
                categoryIds = categoryIds
            )
        }

        assertEquals("상품 원가는 필수입니다.", exception.message)
    }

    @Test
    fun `상품 재고가 0 이하면 예외 발생`() {
        // given
        val name = "테스트 상품"
        val originalPrice = BigDecimal("10000")
        val stock = 0
        val categoryIds = listOf(CategoryId(1L))

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            ProductRegisterCommand(
                name = name,
                originalPrice = originalPrice,
                currentPrice = null,
                stock = stock,
                categoryIds = categoryIds
            )
        }

        assertEquals("상품 재고는 필수입니다.", exception.message)
    }

    @Test
    fun `상품 카테고리가 비어있으면 예외 발생`() {
        // given
        val name = "테스트 상품"
        val originalPrice = BigDecimal("10000")
        val stock = 100
        val categoryIds = emptyList<CategoryId>()

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            ProductRegisterCommand(
                name = name,
                originalPrice = originalPrice,
                currentPrice = null,
                stock = stock,
                categoryIds = categoryIds
            )
        }

        assertEquals("상품 카테고리는 필수입니다.", exception.message)
    }

    @Test
    fun `현재 가격이 null이어도 생성 가능`() {
        // given
        val name = "테스트 상품"
        val originalPrice = BigDecimal("10000")
        val currentPrice = null
        val stock = 100
        val categoryIds = listOf(CategoryId(1L))

        // when
        val command = ProductRegisterCommand(
            name = name,
            originalPrice = originalPrice,
            currentPrice = currentPrice,
            stock = stock,
            categoryIds = categoryIds
        )

        // then
        assertNull(command.currentPrice)
        assertEquals(originalPrice, command.originalPrice)
    }

    @Test
    fun `여러 카테고리를 가진 상품 등록 가능`() {
        // given
        val name = "테스트 상품"
        val originalPrice = BigDecimal("10000")
        val stock = 100
        val categoryIds = listOf(CategoryId(1L), CategoryId(2L), CategoryId(3L))

        // when
        val command = ProductRegisterCommand(
            name = name,
            originalPrice = originalPrice,
            currentPrice = null,
            stock = stock,
            categoryIds = categoryIds
        )

        // then
        assertEquals(3, command.categoryIds.size)
        assertTrue(command.categoryIds.containsAll(categoryIds))
    }
}