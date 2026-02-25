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
import com.minimart.common.event.application.InventoryReserveCompleteEvent
import com.minimart.common.event.kafka.EventTopic
import com.minimart.common.exception.OutBoxWriteException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
        if (eventOutBoxFinder.existsByEventId(command.eventId)) {
            return
        }

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

    private fun publishApplicationEvent(command: ProductReserveCommand, isSuccess: Boolean) {
        applicationEventPublisher.publishEvent(
            InventoryReserveCompleteEvent(
                eventId = command.eventId,
                orderId = command.orderId,
                isSuccess = isSuccess
            )
        )
    }
}