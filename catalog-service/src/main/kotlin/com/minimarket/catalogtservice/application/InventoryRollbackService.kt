package com.minimarket.catalogtservice.application

import com.minimarket.catalogtservice.application.dto.InventoryRollbackCommand
import com.minimarket.catalogtservice.application.`in`.InventoryRollbackUseCase
import com.minimarket.catalogtservice.application.out.EventOutBoxFinder
import com.minimarket.catalogtservice.application.out.EventOutBoxWriter
import com.minimarket.catalogtservice.application.out.ProductFinder
import com.minimarket.catalogtservice.application.out.StockManager
import com.minimarket.catalogtservice.domain.ProductApiException
import com.minimart.common.event.kafka.EventTopic
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun rollback(command: InventoryRollbackCommand) {
        logger.info("[Catalog] 재고 롤백 시작 - orderId: ${command.orderId}, items: ${command.orderLines.size}")

        if (eventOutBoxFinder.existsByEventId(command.eventId)) {
            logger.info("[Catalog] 중복 이벤트 무시 - eventId: ${command.eventId}")
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
            logger.info("[Catalog] 재고 롤백 완료 - orderId: ${command.orderId}")
        } catch (e: Exception) {
            logger.error("[Catalog] 재고 롤백 실패 - orderId: ${command.orderId}", e)
            throw RuntimeException("Failed to rollback inventory for order ${command.orderId}", e)
        }
    }
}