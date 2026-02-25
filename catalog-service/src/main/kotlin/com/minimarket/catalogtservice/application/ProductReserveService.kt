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
import com.minimarket.catalogtservice.domain.event.InventoryReserveCompletedEvent
import com.minimart.common.event.EventTopic
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductReserveService(
    private val stockManager: StockManager,
    private val productFinder: ProductFinder,
    private val eventOutBoxFinder: EventOutBoxFinder,
    private val eventOutBoxWriter: EventOutBoxWriter,
    private val eventPublisher: ApplicationEventPublisher,
): ProductReserveUseCase {

    @Transactional
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
                writeOutBox(command, EventTopic.INVENTORY_FAILED)

                // Spring Event 발행 (트랜잭션 커밋 후 Kafka로 발행됨)
                eventPublisher.publishEvent(
                    InventoryReserveCompletedEvent(
                        eventId = command.eventId,
                        eventType = EventTopic.INVENTORY_FAILED,
                        orderId = command.orderId
                    )
                )
                return
            }
        }

        // 예약 성공 이벤트 아웃박스에 저장
        writeOutBox(command, EventTopic.INVENTORY_RESERVED)

        // Spring Event 발행 (트랜잭션 커밋 후 Kafka로 발행됨)
        eventPublisher.publishEvent(
            InventoryReserveCompletedEvent(
                eventId = command.eventId,
                eventType = EventTopic.INVENTORY_RESERVED,
                orderId = command.orderId
            )
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