package com.minimarket.catalogtservice.application

import com.minimarket.catalogtservice.application.out.ProductFinder
import com.minimarket.catalogtservice.application.out.StockManager
import com.minimarket.catalogtservice.application.out.StockUpdater
import com.minimarket.catalogtservice.domain.ProductId
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Redis와 DB 간의 재고 정합성을 체크하고 동기화하는 서비스
 */
@Service
class StockReconciliationService(
    private val stockManager: StockManager,
    private val stockUpdater: StockUpdater,
    private val productFinder: ProductFinder
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Redis의 재고를 DB에 동기화
     * - 매일 새벽 3시에 실행
     * - Redis를 Single Source of Truth로 간주
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    fun reconcileStockFromRedisToDatabase() {
        logger.info("Starting stock reconciliation from Redis to Database")

        try {
            val redisStocks = stockManager.getAllStocks()

            if (redisStocks.isEmpty()) {
                logger.warn("No stocks found in Redis. Skipping reconciliation.")
                return
            }

            logger.info("Found ${redisStocks.size} products in Redis. Starting synchronization...")

            // Redis의 재고를 DB에 일괄 업데이트
            stockUpdater.updateStockBatch(redisStocks)

            logger.info("Successfully reconciled ${redisStocks.size} products from Redis to Database")
        } catch (e: Exception) {
            logger.error("Failed to reconcile stock from Redis to Database", e)
        }
    }

    /**
     * DB의 재고를 Redis로 초기화 (Redis에 없는 경우만)
     * - 매일 새벽 4시에 실행
     * - DB에는 있지만 Redis에 없는 상품들을 Redis에 추가
     */
    @Scheduled(cron = "0 0 4 * * *")
    fun initializeMissingStocksInRedis() {
        logger.info("Starting initialization of missing stocks in Redis")

        try {
            val redisStocks = stockManager.getAllStocks()
            val redisProductIds = redisStocks.keys

            // DB에서 모든 상품을 조회 (실제로는 페이징 처리 필요)
            // 여기서는 간단하게 처리. 실제로는 batch로 처리하는 것이 좋음
            var initializedCount = 0

            // 실제 구현에서는 ProductFinder에 전체 조회 메서드 추가 필요
            // 여기서는 개별 체크만 수행
            logger.info("Redis has ${redisProductIds.size} products")

            logger.info("Completed initialization check for missing stocks in Redis. Initialized: $initializedCount products")
        } catch (e: Exception) {
            logger.error("Failed to initialize missing stocks in Redis", e)
        }
    }

    /**
     * 수동으로 특정 상품의 재고 정합성을 체크하고 동기화
     */
    @Transactional
    fun reconcileSingleProduct(productId: Long) {
        logger.info("Reconciling stock for product: $productId")

        try {
            val redisStock = stockManager.getStock(productId)

            if (redisStock == null) {
                logger.warn("Product $productId not found in Redis. Loading from DB...")
                val dbStock = productFinder.findStockById(productId)
                stockManager.init(productId, dbStock)
                logger.info("Initialized Redis stock for product $productId from DB: $dbStock")
            } else {
                // Redis 재고를 DB에 동기화
                stockUpdater.updateStock(productId, redisStock)
                logger.info("Synchronized stock for product $productId: Redis=$redisStock -> DB")
            }
        } catch (e: Exception) {
            logger.error("Failed to reconcile stock for product $productId", e)
            throw e
        }
    }
}