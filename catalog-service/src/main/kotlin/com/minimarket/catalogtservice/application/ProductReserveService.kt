package com.minimarket.catalogtservice.application

import com.minimarket.catalogtservice.application.dto.ProductReserveCommand
import com.minimarket.catalogtservice.application.dto.ProductReserveResult
import com.minimarket.catalogtservice.application.`in`.ProductReserveUseCase
import com.minimarket.catalogtservice.application.out.ProductFinder
import com.minimarket.catalogtservice.application.out.StockManager
import com.minimarket.catalogtservice.domain.ProductApiException
import com.minimarket.catalogtservice.domain.ProductErrorCode
import com.minimarket.catalogtservice.domain.ProductErrorCode.NOT_ENOUGH_STOCK
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Service

@Service
class ProductReserveService(
    private val stockManager: StockManager,
    private val productFinder: ProductFinder,
): ProductReserveUseCase {
    override fun reserve(command: ProductReserveCommand): ProductReserveResult {
        command.items.forEach {
            // 재고 데이터가 레디스에 저장되어 있는지부터 검사 후 없으면 저장
            try {
                checkAndSetProductStock(it.productId)
            } catch (e: ProductApiException) {
                return ProductReserveResult(
                    success = false,
                    message = "상품 ID ${it.productId}의 재고 정보를 가져오는 데 실패했습니다."
                )
            }

            val remainingStock = stockManager.decrease(it.productId, it.quantity)
            if (remainingStock < 0) {
                // 재고 부족 시 롤백
                stockManager.add(it.productId, it.quantity)
                return ProductReserveResult(
                    success = false,
                    message = "상품 ID ${it.productId}의 재고가 부족합니다."
                )
            }
        }

        // 예약 성공
        return ProductReserveResult(
            success = true,
        )
    }

    fun checkAndSetProductStock(productId: Long) {
        if (!stockManager.check(productId)) {
            val stock = productFinder.findStockById(productId)

            if (stock < 0) {
                throw ProductApiException(NOT_ENOUGH_STOCK)
            } else {
                stockManager.init(productId, stock)
            }
        }
    }
}