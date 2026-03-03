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
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.event.kafka.InventoryFailed
import com.minimart.common.event.kafka.InventoryReserved
import com.minimart.common.exception.OutBoxWriteException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class InventoryReserveService(
    private val stockManager: StockManager,
    private val productFinder: ProductFinder,
    private val eventOutBoxFinder: EventOutBoxFinder,
    private val eventOutBoxWriter: EventOutBoxWriter,
    private val applicationEventPublisher: ApplicationEventPublisher
): ProductReserveUseCase {

    @Transactional
    override fun reserve(command: ProductReserveCommand) {
        // 이벤트 키 멱등성 검사
        if (eventOutBoxFinder.existsByEventId(command.eventId)) {
            return
        }

        //TODO: 레디스와 디비 작업을 최대한 원자성을 보장
        // 스프링 트랜잭션 이벤트 리스너를 커밋 이후로 실행
        // 처리되지 않은 아웃박스 이벤트를 주기적으로 처리하는 백그라운드 작업을 별도로 운영
        val reservedItems = mutableListOf<ProductReserveItem>()
        command.items.forEach {
            try {
                checkAndSetProductStock(it.productId)
                reserveStock(it)
                reservedItems.add(it)
            } catch (e: ProductApiException) {
                rollbackStocks(reservedItems)
                try {
                    writeOutBox(command, EventTopic.INVENTORY_FAILED)
                    publishApplicationEvent(command, false)
                    return
                } catch (e: Exception) {
                    throw OutBoxWriteException(e)
                }
            }
        }

        try {
            writeOutBox(command, EventTopic.INVENTORY_RESERVED)
            publishApplicationEvent(command, true)
        } catch (e: Exception) {
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
            command.orderId,
            processedAt = LocalDateTime.now() // Redis와 원자성을 맞추기 때문에 이벤트 발생 시점으로 기록한다.
        )
    }

    private fun publishApplicationEvent(command: ProductReserveCommand, isSuccess: Boolean) {
        when (isSuccess) {
            true -> {
                applicationEventPublisher.publishEvent(
                    InventoryReserved(
                        orderId = command.orderId,
                    )
                )
            }
            else -> {
                applicationEventPublisher.publishEvent(
                    InventoryFailed(
                        orderId = command.orderId,
                    )
                )
            }
        }

    }
}