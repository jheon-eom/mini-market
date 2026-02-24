package com.minimarket.catalogtservice.application

import com.minimarket.catalogtservice.application.dto.ProductReserveCommand
import com.minimarket.catalogtservice.application.dto.ProductReserveItem
import com.minimarket.catalogtservice.application.`in`.ProductReserveUseCase
import com.minimarket.catalogtservice.application.out.EventOutBoxFinder
import com.minimarket.catalogtservice.application.out.EventOutBoxWriter
import com.minimarket.catalogtservice.application.out.ProductFinder
import com.minimarket.catalogtservice.application.out.StockManager
import com.minimarket.catalogtservice.domain.ProductApiException
import com.minimarket.catalogtservice.domain.ProductErrorCode.NOT_ENOUGH_STOCK
import com.minimart.common.event.EventTopic
import com.minimart.common.exception.OutBoxWriteException
import org.springframework.stereotype.Service

@Service
class ProductReserveService(
    private val stockManager: StockManager,
    private val productFinder: ProductFinder,
    private val eventOutBoxFinder: EventOutBoxFinder,
    private val eventOutBoxWriter: EventOutBoxWriter,
): ProductReserveUseCase {

    override fun reserve(command: ProductReserveCommand) {
        // 멱등성 보장: 이미 같은 주문 ID로 이벤트가 존재하면 예약 로직을 수행하지 않음
        if (eventOutBoxFinder.existsByEventId(command.eventId)) {
            return
        }

        val reservedItems = mutableListOf<ProductReserveItem>()
        command.items.forEach {
            try {
                // 재고 데이터가 레디스에 저장되어 있는지부터 검사 후 없으면 저장
                checkAndSetProductStock(it.productId)
                reserveStock(it)
                reservedItems.add(it)
            } catch (e: ProductApiException) {
                // 재고 부족, 이전 예약된 상품이 있다면 롤백
                rollbackStocks(reservedItems)

                // 예약 실패 이벤트 아웃박스에 저장
                try {
                    writeOutBox(command, EventTopic.INVENTORY_FAILED)
                    return
                } catch (e: Exception) {
                    throw OutBoxWriteException(e)
                }
            }
        }

        // 예약 성공 이벤트 아웃박스에 저장
        try {
            writeOutBox(command, EventTopic.INVENTORY_RESERVED)
        } catch (e: Exception) {
            // 이벤트 저장 실패 시 롤백
            rollbackStocks(reservedItems)
            throw OutBoxWriteException(e)
        }
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

    private fun reserveStock(item: ProductReserveItem) {
        val remainingStock = stockManager.decrease(item.productId, item.quantity)
        if (remainingStock < 0) {
            // 재고 부족 시 롤백
            stockManager.add(item.productId, item.quantity)
            throw ProductApiException(NOT_ENOUGH_STOCK)
        }
    }

    private fun rollbackStocks(reservedItems: MutableList<ProductReserveItem>) {
        reservedItems.forEach { reservedItem ->
            stockManager.add(reservedItem.productId, reservedItem.quantity)
        }
    }

    private fun writeOutBox(command: ProductReserveCommand, eventType: String) {
        eventOutBoxWriter.write(
            eventId = command.eventId,
            eventType = eventType,
            command.orderId
        )
    }
}