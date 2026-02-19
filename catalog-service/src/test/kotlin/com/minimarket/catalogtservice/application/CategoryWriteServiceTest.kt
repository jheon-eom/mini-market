package com.minimarket.catalogtservice.application

import com.minimarket.catalogtservice.application.dto.CategoryCreateCommand
import com.minimarket.catalogtservice.application.dto.CategoryUpdateCommand
import com.minimarket.catalogtservice.application.out.CategoryFinder
import com.minimarket.catalogtservice.application.out.CategoryWriter
import com.minimarket.catalogtservice.domain.CategoryApiException
import com.minimarket.catalogtservice.domain.CategoryId
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest

@DataJpaTest
class CategoryWriteServiceTest {
    @Autowired private lateinit var categoryWriter: CategoryWriter
    @Autowired private lateinit var categoryFinder: CategoryFinder
    @Autowired private lateinit var entityManager: EntityManager

    private val categoryWriteService: CategoryWriteService by lazy  {
        CategoryWriteService(
            categoryFinder = categoryFinder,
            categoryWriter = categoryWriter,
        )
    }

    @Test
    fun `카테고리 정상적인 생성 테스트`() {
        // given
        val command = CategoryCreateCommand(name = "카테고리1")

        // when
        val created = categoryWriteService.create(command)

        // then
        assertNotNull(categoryFinder.findById(CategoryId(created.id)))
    }

    @Test
    fun `중복된 이름의 카테고리는 생성 실패한다`() {
        // given
        val command = CategoryCreateCommand(name = "카테고리1")
        categoryWriteService.create(command)

        // when & then
        assertThrows<CategoryApiException> {
            categoryWriteService.create(command)
        }
    }

    @Test
    fun `카테고리 이름 변경 테스트`() {
        // given
        val command = CategoryCreateCommand(name = "카테고리1")
        val created = categoryWriteService.create(command)

        // when
        categoryWriteService.update(CategoryUpdateCommand(id = created.id, name = "카테고리2"))
        entityManager.flush()

        // then
        val renamed = categoryFinder.findById(CategoryId(created.id))
        assert(renamed!!.name == "카테고리2")
    }
}