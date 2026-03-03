package com.minimarket.catalogtservice.application

import com.minimarket.catalogtservice.application.dto.InventoryRollbackCommand
import com.minimarket.catalogtservice.application.`in`.InventoryRollbackUseCase
import com.minimarket.catalogtservice.application.out.ProductFinder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class InventoryRollbackService(
    private val productFinder: ProductFinder
): InventoryRollbackUseCase {
    override fun rollback(command: InventoryRollbackCommand) {
        val products = productFinder.findAllByOrderId(command.orderId)
    }
}