package com.minimarket.catalogtservice.application

import com.minimarket.catalogtservice.adapter.out.persistence.jpa.EventOutBoxJpaFinder
import com.minimarket.catalogtservice.adapter.out.persistence.jpa.EventOutBoxJpaWriter
import com.minimarket.catalogtservice.adapter.out.persistence.jpa.EventOutBoxRepository
import com.minimarket.catalogtservice.adapter.out.persistence.jpa.ProductJpaFinder
import com.minimarket.catalogtservice.adapter.out.persistence.jpa.ProductQueryDslRepository
import com.minimarket.catalogtservice.adapter.out.persistence.jpa.ProductRepository
import com.minimarket.catalogtservice.adapter.out.persistence.jpa.ProductCategoryRepository
import com.minimarket.catalogtservice.adapter.out.persistence.jpa.CategoryRepository
import com.minimarket.catalogtservice.adapter.out.persistence.jpa.entity.ProductEntity
import com.minimarket.catalogtservice.adapter.out.persistence.redis.StockRedisManager
import com.minimarket.catalogtservice.application.dto.ProductReserveCommand
import com.minimarket.catalogtservice.application.dto.ProductReserveItem
import com.minimart.common.event.kafka.EventTopic
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class InventoryReserveServiceTest {
    companion object {
        @Container
        @JvmStatic
        val redis: GenericContainer<*> = GenericContainer("redis:7-alpine")
            .withExposedPorts(6379)
    }

    @Autowired private lateinit var productRepository: ProductRepository
    @Autowired private lateinit var productQueryDslRepository: ProductQueryDslRepository
    @Autowired private lateinit var productCategoryRepository: ProductCategoryRepository
    @Autowired private lateinit var categoryRepository: CategoryRepository
    @Autowired private lateinit var eventOutBoxRepository: EventOutBoxRepository
    @Autowired private lateinit var applicationEventPublisher: ApplicationEventPublisher

    private lateinit var redisTemplate: RedisTemplate<String, Any>
    private lateinit var stockManager: StockRedisManager
    private lateinit var inventoryReserveService: InventoryReserveService

    @BeforeEach
    fun setUp() {
        initRedis()
        initDependencies()
        cleanUp()
    }

    @Test
    fun `레디스에 재고가 없을 경우 DB에서 재고를 가져와 등록하고 예약한다`() {
        // given
        val product = createProduct(stock = 100)
        val command = createCommand(eventId = "event-001", product.id!! to 10)

        // when
        inventoryReserveService.reserve(command)

        // then
        assertRedisStockExists(product.id!!)
        assertRedisStock(product.id!!, expectedStock = 90)
        assertOutBoxSaved(eventId = "event-001", expectedType = EventTopic.INVENTORY_RESERVED)
    }

    @Test
    fun `레디스에 재고가 있고 정상적으로 예약된다`() {
        // given
        val product = createProductWithRedisStock(stock = 50)
        val command = createCommand(eventId = "event-002", product.id!! to 5)

        // when
        inventoryReserveService.reserve(command)

        // then
        assertRedisStock(product.id!!, expectedStock = 45)
        assertOutBoxSaved(eventId = "event-002", expectedType = EventTopic.INVENTORY_RESERVED)
    }

    @Test
    fun `레디스에 재고가 부족할 경우 실패 아웃박스를 저장한다`() {
        // given
        val product = createProductWithRedisStock(stock = 10)
        val command = createCommand(eventId = "event-003", product.id!! to 20)

        // when
        inventoryReserveService.reserve(command)

        // then
        assertRedisStock(product.id!!, expectedStock = 10)
        assertOutBoxSaved(eventId = "event-003", expectedType = EventTopic.INVENTORY_FAILED)
    }

    @Test
    fun `여러 상품 예약 중 일부 실패 시 이전 예약을 모두 롤백한다`() {
        // given
        val product1 = createProductWithRedisStock(stock = 100)
        val product2 = createProductWithRedisStock(stock = 100)
        val product3 = createProductWithRedisStock(stock = 5)

        val command = createCommand(
            eventId = "event-004",
            product1.id!! to 10,
            product2.id!! to 20,
            product3.id!! to 30  // 재고 부족으로 실패
        )

        // when
        inventoryReserveService.reserve(command)

        // then
        assertRedisStock(product1.id!!, expectedStock = 100)
        assertRedisStock(product2.id!!, expectedStock = 100)
        assertRedisStock(product3.id!!, expectedStock = 5)
        assertOutBoxSaved(eventId = "event-004", expectedType = EventTopic.INVENTORY_FAILED)
    }

    @Test
    fun `재고 예약 성공 시 아웃박스 테이블에 성공 이벤트를 저장한다`() {
        // given
        val product = createProductWithRedisStock(stock = 200)
        val command = createCommand(eventId = "event-005", product.id!! to 50)

        // when
        inventoryReserveService.reserve(command)

        // then
        assertOutBoxDetails(
            eventId = "event-005",
            orderId = "order-005",
            eventType = EventTopic.INVENTORY_RESERVED
        )
    }

    @Test
    fun `재고 예약 실패 시 아웃박스 테이블에 실패 이벤트를 저장한다`() {
        // given
        val product = createProductWithRedisStock(stock = 10)
        val command = createCommand(eventId = "event-006", product.id!! to 100)

        // when
        inventoryReserveService.reserve(command)

        // then
        assertOutBoxDetails(
            eventId = "event-006",
            orderId = "order-006",
            eventType = EventTopic.INVENTORY_FAILED
        )
    }

    // === Setup 헬퍼 메서드 ===
    private fun initRedis() {
        val connectionFactory = LettuceConnectionFactory(redis.host, redis.getMappedPort(6379))
            .apply { afterPropertiesSet() }

        redisTemplate = RedisTemplate<String, Any>().apply {
            this.connectionFactory = connectionFactory
            keySerializer = StringRedisSerializer()
            valueSerializer = StringRedisSerializer()
            afterPropertiesSet()
        }

        redisTemplate.execute { connection ->
            connection.serverCommands().flushAll()
            null
        }
    }

    private fun initDependencies() {
        stockManager = StockRedisManager(redisTemplate)
        val productFinder = ProductJpaFinder(
            productQueryDslRepository,
            productCategoryRepository,
            categoryRepository,
            productRepository
        )
        val eventOutBoxFinder = EventOutBoxJpaFinder(eventOutBoxRepository)
        val eventOutBoxWriter = EventOutBoxJpaWriter(eventOutBoxRepository)

        inventoryReserveService = InventoryReserveService(
            stockManager = stockManager,
            productFinder = productFinder,
            eventOutBoxFinder = eventOutBoxFinder,
            eventOutBoxWriter = eventOutBoxWriter,
            applicationEventPublisher = applicationEventPublisher
        )
    }

    private fun cleanUp() {
        eventOutBoxRepository.deleteAll()
        productRepository.deleteAll()
    }

    // === 테스트 데이터 생성 헬퍼 메서드 ===
    private fun createProduct(stock: Int): ProductEntity {
        return productRepository.save(
            ProductEntity(
                name = "테스트상품",
                originalPrice = BigDecimal("10000"),
                currentPrice = BigDecimal("10000"),
                stock = stock
            )
        )
    }

    private fun createProductWithRedisStock(stock: Int): ProductEntity {
        val product = createProduct(stock)
        stockManager.init(product.id!!, stock)
        return product
    }

    private fun createCommand(eventId: String, vararg items: Pair<Long, Int>): ProductReserveCommand {
        val orderId = eventId.replace("event", "order")
        return ProductReserveCommand(
            eventId = eventId,
            orderId = orderId,
            items = items.map { (productId, quantity) ->
                ProductReserveItem(productId = productId, quantity = quantity)
            }
        )
    }

    // === 검증 헬퍼 메서드 ===
    private fun assertRedisStockExists(productId: Long) {
        assertTrue(stockManager.check(productId), "레디스에 재고가 등록되어야 합니다")
    }

    private fun assertRedisStock(productId: Long, expectedStock: Int) {
        val actualStock = redisTemplate.opsForValue().get("product:stock:$productId")
        assertEquals(expectedStock.toString(), actualStock, "재고가 $expectedStock 이어야 합니다")
    }

    private fun assertOutBoxSaved(eventId: String, expectedType: String) {
        assertTrue(eventOutBoxRepository.existsByEventId(eventId), "아웃박스가 저장되어야 합니다")
        val outBox = eventOutBoxRepository.findAll().first()
        assertEquals(expectedType, outBox.eventType, "이벤트 타입이 $expectedType 이어야 합니다")
    }

    private fun assertOutBoxDetails(eventId: String, orderId: String, eventType: String) {
        val outBoxes = eventOutBoxRepository.findAll()
        assertEquals(1, outBoxes.size, "아웃박스가 1개 저장되어야 합니다")

        val outBox = outBoxes.first()
        assertEquals(eventId, outBox.eventId)
        assertEquals(eventType, outBox.eventType)
        assertEquals(orderId, outBox.relationId)
        assertNotNull(outBox.processedAt, "처리 시간이 기록되어야 합니다")
    }
}
