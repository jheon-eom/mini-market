package com.minimarket.catalogtservice.application

import com.minimarket.catalogtservice.application.dto.InventoryRollbackCommand
import com.minimarket.catalogtservice.application.`in`.InventoryRollbackUseCase
import com.minimarket.catalogtservice.application.out.EventOutBoxFinder
import com.minimarket.catalogtservice.application.out.EventOutBoxWriter
import com.minimarket.catalogtservice.application.out.ProductFinder
import com.minimarket.catalogtservice.application.out.StockManager
import com.minimarket.catalogtservice.domain.ProductApiException
import com.minimart.common.event.kafka.EventTopic
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class InventoryRollbackService(
    private val eventOutBoxFinder: EventOutBoxFinder,
    private val eventOutBoxWriter: EventOutBoxWriter,
    private val stockManager: StockManager,
): InventoryRollbackUseCase {
    override fun rollback(command: InventoryRollbackCommand) {
        if (eventOutBoxFinder.existsByEventId(command.eventId)) {
            return
        }

        eventOutBoxWriter.write(
            command.eventId,
            EventTopic.ORDER_FAILED,
            command.orderId,
            LocalDateTime.now(),
        )

        try {
            for (line in command.orderLines) {
                stockManager.add(line.productId, line.quantity)
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to rollback inventory for order ${command.orderId}", e)
        }
    }
}